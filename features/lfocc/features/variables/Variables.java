package lfocc.features.variables;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Variables extends Feature {
	
	// TODO: add support for 'null'
	
	public static final String VARIABLES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/variables/ConfigSchema.xsd";
	
	private boolean functionParameters = false;
	private boolean locals = false;
	private boolean globals = false;
	private boolean classMembers = false;

	public void configure(File config) {
		if (config == null)
			return;

		Document cfg = XML.load(config, new File(VARIABLES_CONFIGURATION_SCHEMA));
		functionParameters = XML.getBooleanOption(cfg, "FunctionParameters");
		locals = XML.getBooleanOption(cfg, "Locals");
		globals = XML.getBooleanOption(cfg, "Globals");
		classMembers = XML.getBooleanOption(cfg, "ClassMembers");
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
			extender.addSyntaxRule("variableParameterDeclaration");
			extender = (ExtenderService) services.getService("Functions", "CallExtender");
			extender.addSyntaxRule("variableParameterExpression" +
					"   {\n" +
					"      $$ = $variableParameterExpression;\n" +
					"   }\n"
					);
		}

		if (services.hasFeature("Expressions")) {
			ExtenderService extender = (ExtenderService) services.getService("Expressions", "ExpressionExtender");
			extender.addSyntaxRule(
					"variableUse\n" +
					"   {\n" +
					"   	$$ = $variableUse; \n" +
					"   }\n"
					);
			// FIXME: this should only be registered if assignments (statements) are activated!
			extender = (ExtenderService) services.getService("Expressions", "AssignableExpressionExtender");
			extender.addSyntaxRule("variableUse");
		}

		if (classMembers) {
			ExtenderService extender = (ExtenderService) services.getService("Classes", "Extender");
			extender.addSyntaxRule("variableDeclaration ';' ");
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		cg.getParserGenerator().addParserSource(getName(), generateParserSource(cg));

		cg.addSource("lfocc.features.variables.ast", new File("features/lfocc/features/variables/ast/Variable.java"));
		cg.addSource("lfocc.features.variables.ast", new File("features/lfocc/features/variables/ast/Attribute.java"));

		cg.getParserGenerator().addImport("lfocc.features.variables.ast.*");
	}
	
	private String generateParserSource(FrameworkInterface framework) {
		String src = "";
		src += "variableDeclaration ::= identifier _variableDeclaration ;\n";
		src += "\n";
		src += "_variableDeclaration ::= \n";
		src += "   identifier\n";
		src += "   | identifier ',' _variableDeclaration\n";
		src += "   ;\n";
		src += "\n";
		src += "variableUse (Expression) ::= \n";
		src += "   identifier\n";
		src += "   {\n";
		src += "      $$ = new Variable($identifier); \n";
		src += "   }\n";
		if (classMembers && framework.hasFeature("Expressions")) {
			src += "   | expression '.' identifier\n";
			src += "   {\n";
			src += "      $$ = new Attribute($expression, $identifier); \n";
			src += "   }\n";
		}
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "variableParameterDeclaration ::= \n";
		src += "   # empty\n";
		src += "   | _variableParameterDeclaration\n";
		src += "   ;\n";
		src += "\n";
		src += "_variableParameterDeclaration ::= \n";
		src += "   variableParameterDeclarationElement\n";
		src += "   | variableParameterDeclarationElement ',' _variableParameterDeclaration\n";
		src += "   ;\n";
		src += "\n";
		src += "variableParameterDeclarationElement ::= \n";
		src += "   identifier identifier\n";
		src += "   ;\n";
		src += "\n";
		src += "variableParameterExpression (List<Expression>) ::=\n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<Expression>();\n";
		src += "   }\n";
		src += "   \n";
		src += "   | _variableParameterExpression\n";
		src += "   {\n";
		src += "      $$ = $_variableParameterExpression;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "_variableParameterExpression (List<Expression>) ::=\n";
		src += "   expression\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<Expression>(Arrays.asList($expression));\n";
		src += "   }\n";
		src += "   \n";
		src += "   | expression ',' _variableParameterExpression\n";
		src += "   {\n";
		src += "      $_variableParameterExpression#1.add($expression);\n";
		src += "      $$ = $_variableParameterExpression#1;\n";
		src += "   }\n";
		src += "   ;\n";
		return src;
	}

}
