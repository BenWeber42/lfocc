package lfocc.features.variables;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Variables extends Feature {
	
	// TODO: add support for 'null'
	
	public static final String VARIABLES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/variables/configSchema.xsd";
	
	private boolean functionParameters = false;
	private boolean locals = false;
	private boolean globals = false;
	private boolean classMembers = false;

	public void configure(File config) {
		if (config == null)
			return;

		Document cfg = XML.load(config, new File(VARIABLES_CONFIGURATION_SCHEMA));
		functionParameters = cfg.getElementsByTagName("FunctionParameters").item(0).getTextContent().equals("true");
		locals = cfg.getElementsByTagName("Locals").item(0).getTextContent().equals("true");
		globals = cfg.getElementsByTagName("Globals").item(0).getTextContent().equals("true");
		classMembers = cfg.getElementsByTagName("ClassMembers").item(0).getTextContent().equals("true");
	}

	public List<String> getConfiguration() {
		return Arrays.asList(
				"FunctionParameters = " + functionParameters,
				"FunctionLocals = " + locals,
				"Globals = " + globals,
				"ClassMembers = " + classMembers
				);
	}

	@Override
	public void setup(FeatureHelper helper) {
		configure(helper.getConfiguration());
		helper.printConfiguration(getConfiguration());
		
		helper.depends("SyntaxBase");
		if (classMembers)
			helper.depends("Classes");
		
		if (globals)
			helper.depends("GlobalScope");
		
		if (locals)
			helper.depends("Statement");

		if (functionParameters) {
			helper.depends("Functions");
			// can't assign function parameter values without expressions
			helper.depends("Expressions");
		}
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (globals) {
			ExtenderService extender = (ExtenderService) services.getService("GlobalScope", "Extender");
			extender.addSyntaxRule("variableDeclaration ';' ");
		}
		
		if (locals) {
			ExtenderService extender = (ExtenderService) services.getService("Statement", "Extender");
			extender.addSyntaxRule("variableDeclaration");
		}
		
		if (functionParameters) {
			ExtenderService extender = (ExtenderService) services.getService("Functions", "DeclarationExtender");
			extender.addSyntaxRule("variableDeclaration ( ',' variableDeclaration )*");
			extender = (ExtenderService) services.getService("Functions", "CallExtender");
			extender.addSyntaxRule("expression ( ',' expression )*");
		}

		/*
		 * The Classes featuer will register all ObjectProviders as expressions.
		 * So if the Classes feature is activeted we don't have to register variable
		 * usage as expression.
		 * 
		 * If the Classes feature is deactivated and the Expressions feature is
		 * activated, we have to register variable usage as expression ourself.
		 */

		if (!services.hasFeature("Classes") && services.hasFeature("Expressions")) {
			ExtenderService extender = (ExtenderService) services.getService("Expressions", "Extender");
			extender.addSyntaxRule("variableUse");
			return;
		}

		if (services.hasFeature("Classes")) {
			ExtenderService extender = (ExtenderService) services.getService("Classes", "ObjectProvider");
			extender.addSyntaxRule("variableUse");
		}

		if (classMembers) {
			ExtenderService extender = (ExtenderService) services.getService("Classes", "BodyExtender");
			extender.addSyntaxRule("variableDeclaration ';' ");
			extender = (ExtenderService) services.getService("Classes", "ObjectMember");
			extender.addSyntaxRule("variableUse");
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
	}
	
	private String generateParserSource() {
		String src = "";
		src += "variableDeclaration : Identifier Identifier ;\n";
		src += "\n";
		src += "parameterDeclaration : Identifier Identifier\n";
		src += "   ( ',' Identifier Identifier )* ;\n";
		src += "\n";
		src += "variableUse : Identifier ;\n";
		return src;
	}

}
