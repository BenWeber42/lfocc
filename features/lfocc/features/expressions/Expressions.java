package lfocc.features.expressions;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.feature.MultiExtendable;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.feature.service.ServiceProvider;

public class Expressions extends MultiExtendable {
	
	private static final String expressionExtender = "ExpressionExtender";
	private static final String assignableExpressionExtender = "AssignableExpressionExtender";

	public Expressions() {
		super(new HashSet<String>(Arrays.asList(
				expressionExtender, assignableExpressionExtender
				)));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender(expressionExtender));
		helper.registerService(getExtender(assignableExpressionExtender));
		
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		if (services.hasFeature("Types")) {
			ExtenderService extender = (ExtenderService) services.getService("Types", "Extender");
			extender.addSyntaxRule(
					"'int'\n" +
					"   {\n" +
					"      $$ = new IntType();\n" +
					"   }\n"
					);
			extender.addSyntaxRule(
					"'float'\n" +
					"   {\n" +
					"      $$ = new FloatType();\n" +
					"   }\n"
					);
			extender.addSyntaxRule(
					"'boolean'\n" +
					"   {\n" +
					"      $$ = new BooleanType();\n" +
					"   }\n"
					);
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		// Lexems
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar(cg));
		cg.getParserGenerator().addToken("'+'", "/\\+/");
		cg.getParserGenerator().addToken("'-'", "/\\-/");
		cg.getParserGenerator().addToken("'*'", "/\\*/");
		cg.getParserGenerator().addToken("'/'", "/\\//");
		cg.getParserGenerator().addToken("'%'", "/%/");
		cg.getParserGenerator().addToken("'&&'", "/&&/");
		cg.getParserGenerator().addToken("'||'", "/\\|\\|/");
		cg.getParserGenerator().addToken("'!'", "/!/");
		cg.getParserGenerator().addToken("'=='", "/==/");
		cg.getParserGenerator().addToken("'!='", "/!=/");
		cg.getParserGenerator().addToken("'<='", "/<=/");
		cg.getParserGenerator().addToken("'<'", "/</");
		cg.getParserGenerator().addToken("'>='", "/>=/");
		cg.getParserGenerator().addToken("'>'", "/>/");
		cg.getParserGenerator().addToken("'int'", "/int/");
		cg.getParserGenerator().addToken("'float'", "/float/");
		cg.getParserGenerator().addToken("'boolean'", "/boolean/");
		cg.getParserGenerator().addPrecedence("'||'", 0);
		cg.getParserGenerator().addPrecedence("'&&'", 1);
		cg.getParserGenerator().addPrecedence("'==' '!='", 2);
		cg.getParserGenerator().addPrecedence("'<=' '>=' '<' '>'", 3);
		cg.getParserGenerator().addPrecedence("'+' '-'", 4);
		cg.getParserGenerator().addPrecedence("'*' '/' '%'", 5);
		cg.getParserGenerator().addPrecedence("'!'", 6);

		// FIXME: integer should also parse negative integers!
		cg.getParserGenerator().addToken("integer", "String", "/0|[1-9][0-9]*/   { $lexem = current(); break; }");
		cg.getParserGenerator().addToken("hex", "String", "/0x[a-fA-F0-9]+/   { $lexem = current().substring(2); break; }");
		cg.getParserGenerator().addToken("float", "String", "/(0|[1-9][0-9]*)\\.[0-9]+/ { $lexem = current(); break; }");
		cg.getParserGenerator().addToken("boolean", "String", "/true|false/ { $lexem = current(); break; }");
		
		// Sources
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/Expression.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/IntConst.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/FloatConst.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/BooleanConst.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/BinaryExpression.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/BinaryOperatorExpression.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/UnaryExpression.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/UnaryOperatorExpression.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/PrimitiveType.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/IntType.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/FloatType.java"));
		cg.addSource("lfocc.features.expressions.ast",
				new File("features/lfocc/features/expressions/ast/BooleanType.java"));
		
		cg.addSource("lfocc.features.expressions.semantics",
				new File("features/lfocc/features/expressions/semantics/SymbolResolver.java"));
		cg.getSemanticsGenerator().addTransformer(3000,
				"lfocc.features.expressions.semantics", "SymbolResolver");

		if (cg.hasFeature("Types")) {
			cg.addSource("lfocc.features.expressions.semantics", 
					new File("features/lfocc/features/expressions/semantics/Primitives.java"));
			cg.addSource("lfocc.features.expressions.semantics", 
					new File("features/lfocc/features/expressions/semantics/OperatorTypeResolver.java"));
			cg.getSemanticsGenerator().addTransformer(500,
					"lfocc.features.expressions.semantics", "Primitives");
			cg.getSemanticsGenerator().addTransformer(4000,
					"lfocc.features.expressions.semantics", "OperatorTypeResolver");
			
			if (cg.hasFeature("Assignments")) {
				cg.addSource("lfocc.features.expressions.semantics", 
						new File("features/lfocc/features/expressions/semantics/PrimitiveAssignmentChecker.java"));
				cg.getSemanticsGenerator().addTransformer(8000,
						"lfocc.features.expressions.semantics", "PrimitiveAssignmentChecker");
			}
		}
		
		// imports:
		cg.getParserGenerator().addImport("lfocc.features.expressions.ast.*");
		cg.getParserGenerator().addImport("java.lang.NumberFormatException");
	}

	private String generateGrammar(FrameworkInterface helper) {
		String src = "";
		
		src += "expression (Expression) ::=\n";
		src += "   '(' expr = expression ')'\n";
		src += "   {\n";
		src += "      $$ = $expr; \n";
		src += "   }\n";
		src += "\n";
		src += "   | integer\n";
		src += "   {\n";
		src += "      try { $$ = new IntConst(Integer.parseInt($integer)); }\n";
		src += "      catch (NumberFormatException e) {\n";
		src += "         reporter.error(${integer.line}, String.format(\"Invalid integer '%s'!\", $integer));\n";
		src += "      }\n";
		src += "   }\n";
		src += "\n";
		src += "   | hex\n";
		src += "   {\n";
		src += "      try { $$ = new IntConst(Integer.parseInt($hex, 16)); }\n";
		src += "      catch (NumberFormatException e) {\n";
		src += "         reporter.error(${hex.line}, String.format(\"Invalid integer '%s'!\", $hex));\n";
		src += "      }\n";
		src += "   }\n";
		src += "\n";
		src += "   | float\n";
		src += "   {\n";
		src += "      try { $$ = new FloatConst(Float.parseFloat($float)); }\n";
		src += "      catch (NumberFormatException e) {\n";
		src += "         reporter.error(${float.line}, String.format(\"Invalid float '%s'!\", $float));\n";
		src += "      }\n";
		src += "   }\n";
		src += "\n";
		src += "   | boolean\n";
		src += "   {\n";
		src += "      $$ = new BooleanConst(\"true\".equals($boolean));\n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '+' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.PLUS, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '-' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.MINUS, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '*' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.TIMES, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '/' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.DIVIDE, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '%' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.MODULO, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '&&' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.AND, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '||' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.OR, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | '!' arg = expression\n";
		src += "   {\n";
		src += "      $$ = new UnaryOperatorExpression(UnaryOperatorExpression.Operator.NOT, $arg); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '==' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.EQUAL, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '!=' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.NOT_EQUAL, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '<=' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.SMALLER_EQUAL, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '<' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.SMALLER, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '>=' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.GREATER_EQUAL, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | left = expression '>' right = expression\n";
		src += "   {\n";
		src += "      $$ = new BinaryOperatorExpression(BinaryOperatorExpression.Operator.GREATER, $left, $right); \n";
		src += "   }\n";
		src += "\n";
		src += "   | '-' arg = expression\n";
		src += "   {\n";
		src += "      $$ = new UnaryOperatorExpression(UnaryOperatorExpression.Operator.MINUS, $arg); \n";
		src += "   }\n";
		src += "\n";
		src += "   | '+' arg = expression\n";
		src += "   {\n";
		src += "      $$ = new UnaryOperatorExpression(UnaryOperatorExpression.Operator.PLUS, $arg); \n";
		src += "   }\n";
		src += "\n";
		
		Iterator<String> it = getExtensions(expressionExtender).iterator();
		while (it.hasNext())
			src += "   | " + it.next() + "\n";
		src += "   ;\n";
		src += "\n";

		if (!helper.hasFeature("Assignments"))
			return src;
		
		src += "assignableExpression (Expression) ::=\n";
		it = getExtensions(assignableExpressionExtender).iterator();

		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			while (it.hasNext())
				src += "   | " + it.next() + "\n";
		}
		src += "   ;\n";
		return src;
	}
}
