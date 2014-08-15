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
			extender.addSyntaxRule(
					"attributeDeclaration\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>($attributeDeclaration);\n" +
					"   }\n"
					);
		}
		
		if (locals) {
			ExtenderService extender = (ExtenderService) services.getService("Statement", "Extender");
			extender.addSyntaxRule(
					"variableDeclaration\n" +
					"   {\n" +
					"      $$ = $variableDeclaration;\n" +
					"   }\n"
					);
		}
		
		if (functionParameters) {
			ExtenderService extender = (ExtenderService) services.getService("Functions", "DeclarationExtender");
			extender.addSyntaxRule(
					"variableParameterDeclaration\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>($variableParameterDeclaration);\n" +
					"   }\n"
					);
			
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

			if (services.hasFeature("Assignments")) {
				extender = (ExtenderService) services.getService("Expressions", "AssignableExpressionExtender");
				extender.addSyntaxRule(
						"variableUse\n" +
						"   {\n" +
						"      $$ = $variableUse;\n" +
						"   }\n"
						);
			}
		}

		if (classMembers) {
			ExtenderService extender = (ExtenderService) services.getService("Classes", "Extender");
			extender.addSyntaxRule(
					"attributeDeclaration" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>($attributeDeclaration);\n" +
					"   }\n"
					);
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar(cg));

		cg.addSource("lfocc.features.variables.ast",
				new File("features/lfocc/features/variables/ast/Variable.java"));
		cg.addSource("lfocc.features.variables.ast",
				new File("features/lfocc/features/variables/ast/Attribute.java"));
		cg.addSource("lfocc.features.variables.ast",
				new File("features/lfocc/features/variables/ast/VariableDeclaration.java"));
		cg.addSource("lfocc.features.variables.ast",
				new File("features/lfocc/features/variables/ast/TypeSymbol.java"));

		cg.getParserGenerator().addImport("lfocc.features.variables.ast.*");
	}
	
	private String generateGrammar(FrameworkInterface framework) {
		String src = "";
		src += "variableDeclaration (List<ASTNode>) ::=\n";
		src += "   type = identifier vars = _variableDeclaration\n";
		src += "   {\n";
		src += "      Iterator<ASTNode> it = $vars.iterator();\n";
		src += "      while (it.hasNext()) {\n";
		src += "         ASTNode node = it.next();\n";
		src += "         if (node instanceof VariableDeclaration)\n";
		src += "            ((VariableDeclaration) node).setType($type);\n";
		src += "      }\n";
		src += "      $$ = $vars;\n";
		src += "   }\n";
		src += "   ;\n";
		src += "\n";
		src += "_variableDeclaration (List<ASTNode>) ::= \n";
		src += "   identifier\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<ASTNode>(Arrays.asList(new VariableDeclaration(null, $identifier)));\n";
		src += "   }\n";
		src += "\n";
		if (framework.hasFeature("Assignments")) {
			src += "   | identifier '=' expression\n";
			src += "   {\n";
			src += "      $$ = new ArrayList<ASTNode>(Arrays.asList(\n";
			src += "         new VariableDeclaration(null, $identifier),\n";
			src += "         new Assignment(new Variable($identifier), $expression)\n";
			src += "      ));\n";
			src += "   }\n";
			src += "\n";
		}
		src += "   | identifier ',' next = _variableDeclaration\n";
		src += "   {\n";
		src += "      $next.add(0, new VariableDeclaration(null, $identifier));\n";
		src += "      $$ = $next;\n";
		src += "   }\n";
		src += "\n";
		if (framework.hasFeature("Assignments")) {
			src += "   | identifier '=' expression ',' next = _variableDeclaration\n";
			src += "   {\n";
			src += "      $next.add(0, new VariableDeclaration(null, $identifier));\n";
			src += "      $next.add(1, new Assignment(new Variable($identifier), $expression));\n";
			src += "      $$ = $next;\n";
			src += "   }\n";
			src += "\n";
		}
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
		src += "attributeDeclaration (List<VariableDeclaration>) ::= \n";
		src += "   type = identifier vars = attributeDeclarations ';'\n";
		src += "   {\n";
		src += "      Iterator<VariableDeclaration> var = $vars.iterator();\n";
		src += "      while (var.hasNext())\n";
		src += "         var.next().setType($type);\n";
		src += "      $$ = $vars;\n";
		src += "   }\n";
		src += "   ;\n";
		src += "\n";
		src += "attributeDeclarations (List<VariableDeclaration>) ::= \n";
		src += "   attributeName\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<VariableDeclaration>(Arrays.asList($attributeName));\n";
		src += "   }\n";
		src += "   \n";
		src += "   | attributeName ',' next = attributeDeclarations\n";
		src += "   {\n";
		src += "      $next.add(0, $attributeName);\n";
		src += "      $$ = $next;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "attributeName (VariableDeclaration) ::=\n";
		src += "   identifier\n";
		src += "   {\n";
		src += "      $$ = new VariableDeclaration(null, $identifier);\n";
		src += "   }\n";
		src += "   ;\n";
		src += "\n";
		if (functionParameters) {
			src += "variableParameterDeclaration (List<VariableDeclaration>) ::= \n";
			src += "   # empty\n";
			src += "   {\n";
			src += "      $$ = new ArrayList<VariableDeclaration>();\n";
			src += "   }\n";
			src += "   \n";
			src += "   | vars = _variableParameterDeclaration\n";
			src += "   {\n";
			src += "      $$ = $vars;\n";
			src += "   }\n";
			src += "   \n";
			src += "   ;\n";
			src += "\n";
			src += "_variableParameterDeclaration (List<VariableDeclaration>) ::= \n";
			src += "   variableParameterDeclarationElement\n";
			src += "   {\n";
			src += "      $$ = new ArrayList<VariableDeclaration>(Arrays.asList($variableParameterDeclarationElement));\n";
			src += "   }\n";
			src += "   \n";
			src += "   | variableParameterDeclarationElement ',' next = _variableParameterDeclaration\n";
			src += "   {\n";
			src += "      $next.add(0, $variableParameterDeclarationElement);\n";
			src += "      $$ = $next;\n";
			src += "   }\n";
			src += "   ;\n";
			src += "\n";
			src += "variableParameterDeclarationElement (VariableDeclaration) ::= \n";
			src += "   type = identifier name = identifier\n";
			src += "   {\n";
			src += "      $$ = new VariableDeclaration($type, $name);\n";
			src += "   }\n";
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
			src += "   | expression ',' next = _variableParameterExpression\n";
			src += "   {\n";
			src += "      $next.add($expression);\n";
			src += "      $$ = $next;\n";
			src += "   }\n";
			src += "   ;\n";
		}

		return src;
	}

}
