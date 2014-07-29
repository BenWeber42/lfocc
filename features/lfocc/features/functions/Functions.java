package lfocc.features.functions;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.feature.MultiExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Functions extends MultiExtendable {
	
	public static final String FUNCTIONS_CONFIGURATION_SCHEMA = 
			"features/lfocc/features/functions/ConfigSchema.xsd";

	private boolean globals = true; // function declarations in global scope
	private boolean classMembers = true; // function declarations as class members
	private boolean returnValue = true; // return value allowed

	private static final String declarationExtender = "DeclarationExtender";
	private static final String callExtender = "CallExtender";
	
	public Functions() {
		super(new HashSet<String>(Arrays.asList(callExtender, declarationExtender)));
	}

	public void setup(FeatureHelper helper) {

		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(FUNCTIONS_CONFIGURATION_SCHEMA));
			globals = XML.getBooleanOption(cfg, "Globals");
			classMembers = XML.getBooleanOption(cfg, "ClassMembers");
			returnValue = XML.getBooleanOption(cfg, "ReturnValue");
		}
		
		helper.printConfiguration(Arrays.asList(
				"Globals = " + globals,
				"ClassMembers = " + classMembers,
				"ReturnValue = " + returnValue));
		
		if (!globals && !classMembers)
			return; // function declarations can't occur anywhere, nothing to do

		helper.depends("CodeBlock");
		helper.depends("SyntaxBase"); // because of '{' or '('
		
		if (globals)
			helper.depends("GlobalScope");
		if (classMembers)
			helper.depends("Classes");
		if (returnValue)
			helper.depends("Expressions");
		
		helper.registerService(getExtender(callExtender));
		// There needs to be some kind of contract between whoever extends the
		// syntax and Functions for type saftey. Currently the contract is that
		// every extension returns a List<Expression> (the list of arguments)
		helper.registerService(getExtender(declarationExtender));
		
	}
	
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (!globals && !classMembers)
			return;

		ExtenderService extender = (ExtenderService) 
				services.getService("CodeBlock", "Extender");
		extender.addSyntaxRule(
				"returnStmt" +
				"   {\n" +
				"      $$ = new ArrayList<ASTNode>(Arrays.asList($returnStmt));\n" +
				"   }\n"
				);
		
		if (globals) {
			extender = (ExtenderService) 
					services.getService("GlobalScope", "Extender");
			extender.addSyntaxRule(
					"functionDeclaration\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($functionDeclaration));\n" +
					"   }\n"
					);
		}

		if (services.hasFeature("Statement")) {
			extender = (ExtenderService) 
					services.getService("Statement", "Extender");
			extender.addSyntaxRule(
					"functionCall" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($functionCall));\n" +
					"   }\n"
					);
		}

		if (services.hasFeature("Expressions")) {
			extender = (ExtenderService) 
					services.getService("Expressions", "ExpressionExtender");
			extender.addSyntaxRule(
					"functionCall" +
					"   {\n" +
					"      $$ = $functionCall;\n" +
					"   }\n"
					);
		}

		if (classMembers) {
			extender = (ExtenderService) 
					services.getService("Classes", "Extender");
			extender.addSyntaxRule(
					"functionDeclaration" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($functionDeclaration));\n" +
					"   }\n"
					);
		}

	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {

		if (!globals && !classMembers)
			return;

		cg.getParserGenerator().addGrammarSource(getName(), generateFunctionGrammar(cg));
		cg.getParserGenerator().addGrammarSource(getName(), generateReturnGrammar());
		cg.getParserGenerator().addToken("'return'", "/return/");
		
		cg.addSource("lfocc.features.functions.ast.FunctionCall",
				new File("features/lfocc/features/functions/ast/FunctionCall.java"));
		cg.addSource("lfocc.features.functions.ast.FunctionCall",
				new File("features/lfocc/features/functions/ast/MethodCall.java"));
		cg.addSource("lfocc.features.functions.ast.FunctionCall",
				new File("features/lfocc/features/functions/ast/ReturnStatement.java"));
		cg.addSource("lfocc.features.functions.ast.FunctionCall",
				new File("features/lfocc/features/functions/ast/FunctionDeclaration.java"));
		
		cg.getParserGenerator().addImport("lfocc.features.functions.ast.*");
	}
	
	private String generateFunctionGrammar(FrameworkInterface framework) {
		String src = "";
		src += "functionDeclaration (FunctionDeclaration) ::=\n";
		src += "   type = identifier name = identifier '(' parameterDeclaration ')' '{' codeBlock '}'\n";
		src += "   {\n";
		src += "      $$ = new FunctionDeclaration($type, $name, $parameterDeclaration, $codeBlock);\n";
		src += "   }\n";
		src += "   ;\n";
		src += "\n";
		src += "parameterDeclaration (List<ASTNode>) ::=\n";
		
		Iterator<String> it = getExtensions(declarationExtender).iterator();
		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			
			while (it.hasNext())
				src += "   | " + it.next() + "\n";

		}
		src += "   ;\n";
		src += "\n";
		src += "functionCall (Expression) ::=\n";
		src += "   identifier '(' parameterExpression ')'\n";
		src += "   {\n";
		src += "      $$ = new FunctionCall($identifier, $parameterExpression);\n";
		src += "   }\n";
		src += "   \n";
		if (classMembers && framework.hasFeature("Expressions")) {
			src += "   | expression '.' identifier '(' parameterExpression ')'\n";
			src += "   {\n";
			src += "      $$ = new MethodCall($identifier, $expression, $parameterExpression);\n";
			src += "   }\n";
			src += "   \n";
		}
		src += "   ;\n";
		src += "\n";
		src += "parameterExpression (List<Expression>) ::=\n";

		it = getExtensions(callExtender).iterator();
		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			
			while (it.hasNext())
				src += "   | " + it.next() + "\n";

		}

		src += "   ;\n";
		
		return src;
	}
	
	private String generateReturnGrammar() {
		String src = "";

		src += "returnStmt (ReturnStatement) ::= \n";
		src += "   'return' ';'\n";
		src += "   {\n";
		src += "      $$ = new ReturnStatement(null);\n";
		src += "   }\n";
		src += "   \n";
		if (returnValue) {
			src += "   | 'return' expression ';'\n";
		src += "   {\n";
		src += "      $$ = new ReturnStatement($expression);\n";
		src += "   }\n";
		src += "   \n";
		}
		src += "   ;\n";
		
		return src;
	}
}
