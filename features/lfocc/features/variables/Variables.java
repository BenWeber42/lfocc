package lfocc.features.variables;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;
import lfocc.framework.util.XML;

public class Variables extends Feature {
	
	public static final String VARIABLES_PARSER_FILE =
			"features/lfocc/features/variables/Variables.g";
	public static final String VARIABLES_PARSER_NAME = "Variables";
	
	public static final String VARIABLES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/variables/configSchema.xsd";
	
	private boolean funcParams = false;
	private boolean funcLocals = false;
	private boolean globals = false;
	private boolean classMembers = false;
	
	private boolean expressions;

	public void configure(File config) {
		if (config == null)
			return;

		Document cfg = XML.load(config, new File(VARIABLES_CONFIGURATION_SCHEMA));
		funcParams = cfg.getElementsByTagName("FunctionParameters").item(0).getTextContent().equals("true");
		funcLocals = cfg.getElementsByTagName("FunctionLocals").item(0).getTextContent().equals("true");
		globals = cfg.getElementsByTagName("Globals").item(0).getTextContent().equals("true");
		classMembers = cfg.getElementsByTagName("ClassMembers").item(0).getTextContent().equals("true");
	}

	public List<String> getConfiguration() {
		return Arrays.asList(
				"FunctionParameters = " + funcParams,
				"FunctionLocals = " + funcLocals,
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
		
		if (funcParams || funcLocals)
			helper.depends("Functions");
		
		expressions = helper.hasFeature("Expressions");
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (classMembers) {
			SyntaxExtender classes = (SyntaxExtender) services.getService("Classes", "SyntaxExtender");
			classes.addSyntaxRule("variableDeclaration");
		}

		if (globals) {
			SyntaxExtender classes = (SyntaxExtender) services.getService("GlobalScope", "SyntaxExtender");
			classes.addSyntaxRule("variableDeclaration");
		}
		
		if (funcLocals) {
			SyntaxExtender classes = (SyntaxExtender) services.getService("CodeBlock", "SyntaxExtender");
			classes.addSyntaxRule("variableDeclaration");
		}
		
		if (expressions) {
			SyntaxExtender classes = (SyntaxExtender) services.getService("Expressions", "SyntaxExtender");
			classes.addSyntaxRule("variableUse");
		}
		
		// TODO: function parameters

	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		cg.getParserGenerator().addParserGrammar(
				getName(), new File(VARIABLES_PARSER_FILE), VARIABLES_PARSER_NAME);
	}

}
