package lfocc.features.variables;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;

import lfocc.features.functions.services.CallExtender;
import lfocc.features.functions.services.DeclarationExtender;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;
import lfocc.framework.util.XML;

public class Variables extends Feature {
	
	// TODO: add support for 'null'
	
	public static final String VARIABLES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/variables/configSchema.xsd";
	
	private boolean functionParameters = false;
	private boolean locals = false;
	private boolean globals = false;
	private boolean classMembers = false;
	
	private boolean expressions;

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
			// can't assign function parameters values without expressions
			helper.depends("Expressions");
		}
		
		expressions = helper.hasFeature("Expressions");
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (classMembers) {
			SyntaxExtender extender = (SyntaxExtender) services.getService("Classes", "SyntaxExtender");
			extender.addSyntaxRule("variableDeclaration ';' ");
		}

		if (globals) {
			SyntaxExtender extender = (SyntaxExtender) services.getService("GlobalScope", "SyntaxExtender");
			extender.addSyntaxRule("variableDeclaration ';' ");
		}
		
		if (locals) {
			SyntaxExtender extender = (SyntaxExtender) services.getService("Statement", "SyntaxExtender");
			extender.addSyntaxRule("variableDeclaration");
		}
		
		if (expressions) {
			SyntaxExtender extender = (SyntaxExtender) services.getService("Expressions", "SyntaxExtender");
			extender.addSyntaxRule("variableUse");
		}
		
		if (functionParameters) {
			DeclarationExtender declarationExtender = (DeclarationExtender) services.getService("Functions", "DeclarationExtender");
			declarationExtender.addSyntaxRule("variableDeclaration ( ',' variableDeclaration )*");
			CallExtender callExtender = (CallExtender) services.getService("Functions", "CallExtender");
			callExtender.addSyntaxRule("expression ( ',' expression )*");
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
