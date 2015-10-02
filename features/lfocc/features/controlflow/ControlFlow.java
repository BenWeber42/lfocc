package lfocc.features.controlflow;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class ControlFlow extends Feature {
	
	public static final String CONTROL_FLOW_CONFIGURATION_SCHEMA =
			"features/lfocc/features/controlflow/ConfigSchema.xsd";

	private boolean ifConditional = true;
	private boolean elseConditional = true;
	private boolean elseIfConditional = true;
	private boolean whileLoop = true;
	private boolean doWhileLoop = true;
	private boolean forLoop = true;
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("CodeBlock");
		helper.depends("Expressions");
		
		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(CONTROL_FLOW_CONFIGURATION_SCHEMA));
			ifConditional = XML.getBooleanOption(cfg, "IfConditional");
			elseConditional = XML.getBooleanOption(cfg, "ElseConditional");
			elseIfConditional = XML.getBooleanOption(cfg, "ElseIfConditional");
			whileLoop = XML.getBooleanOption(cfg, "WhileLoop");
			doWhileLoop = XML.getBooleanOption(cfg, "DoWhileLoop");
			forLoop = XML.getBooleanOption(cfg, "ForLoop");
		}
		
		helper.printConfiguration(Arrays.asList(
				"IfConditional = " + ifConditional,
				"ElseConditional = " + elseConditional,
				"ElseIfConditional = " + elseIfConditional,
				"WhileLoop = " + whileLoop,
				"DoWhileLoop = " + doWhileLoop,
				"ForLoop = " + forLoop));
		
		if (forLoop)
			helper.depends("Statement");
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		ExtenderService codeBlock = (ExtenderService) services.getService(
				"CodeBlock", "Extender");

		if (ifConditional) {
			codeBlock.addSyntaxRule(
					"ifConditional\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($ifConditional));\n" +
					"   }\n"
					);
		}
		if (whileLoop) {
			codeBlock.addSyntaxRule(
					"whileLoop\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($whileLoop));\n" +
					"   }\n"
					);
		}
		if (doWhileLoop) {
			codeBlock.addSyntaxRule(
					"doWhileLoop\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($doWhileLoop));\n" +
					"   }\n"
					);
		}
		if (forLoop) {
			codeBlock.addSyntaxRule(
					"forLoop\n" +
					"   {\n" +
					"      $$ = new ArrayList<ASTNode>(Arrays.asList($forLoop));\n" +
					"   }\n"
					);
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addImport("lfocc.features.controlflow.ast.*");
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/Conditional.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/ConditionalSequence.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/IfConditional.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/ElseConditional.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/WhileLoop.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/DoWhileLoop.java"));
		cg.addSource("lfocc.features.controlflow.ast",
				new File("features/lfocc/features/controlflow/ast/ForLoop.java"));

		if (ifConditional) {
			cg.getParserGenerator().addGrammarSource(getName(), generateIfGrammar());
			cg.getParserGenerator().addToken("'if'", "/if/");
			if (elseConditional | elseIfConditional)
				cg.getParserGenerator().addToken("'else'", "/else/");
		}
		if (whileLoop) {
			cg.getParserGenerator().addGrammarSource(getName(), generateWhileGrammar());
			cg.getParserGenerator().addToken("'while'", "/while/");
		}
		if (doWhileLoop) {
			cg.getParserGenerator().addGrammarSource(getName(), generateDoWhileGrammar());
			cg.getParserGenerator().addToken("'do'", "/do/");
			if (!whileLoop)
				cg.getParserGenerator().addToken("'while'", "/while/");
		}
		if (forLoop) {
			cg.getParserGenerator().addGrammarSource(getName(), generateForGrammar());
			cg.getParserGenerator().addToken("'for'", "/for/");
		}
		
		if (cg.hasFeature("Types")) {
			cg.addSource("lfocc.features.controlflow.semantics",
					new File("features/lfocc/features/controlflow/semantics/ControlFlowChecker.java"));
			cg.getSemanticsGenerator().addTransformer(7000,
					"lfocc.features.controlflow.semantics", "ControlFlowChecker");
		}
	}
	
	private String generateIfGrammar() {
		String src = "";
		src += "ifConditional (ConditionalSequence) ::= \n";
		if (ifConditional) {
			src += "   'if' '(' expression ')' '{' codeBlock '}' ifRest\n";
			src += "   {\n";
			src += "      $ifRest.insert(new IfConditional($expression, $codeBlock));\n";
			src += "      $$ = $ifRest;\n";
			src += "   }\n";
			src += "   ;\n";
		}
		src += "\n";

		src += "ifRest (ConditionalSequence) ::= \n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ConditionalSequence();\n";
		src += "   }\n";
		if (elseConditional || elseIfConditional) {
			src += "   | 'else' ifRest2\n";
			src += "   {\n";
			src += "      $$ = $ifRest2;\n";
			src += "   }\n";
		}
		src += "   ;\n";
		src += "\n";
		src += "ifRest2 (ConditionalSequence) ::=\n";
		if (elseIfConditional) {
			src += "   'if' '(' expression ')' '{' codeBlock '}' ifRest\n";
			src += "   {\n";
			src += "      $ifRest.insert(new IfConditional($expression, $codeBlock));\n";
			src += "      $$ = $ifRest;\n";
			src += "   }\n";
		}
		if (elseConditional) {
			if (elseIfConditional)
				src += "   | '{' codeBlock '}'\n";
			else
				src += "   '{' codeBlock '}'\n";
			src += "   {\n";
			src += "      ConditionalSequence cond = new ConditionalSequence();\n";
			src += "      cond.setElse(new ElseConditional($codeBlock));\n";
			src += "      $$ = cond;\n";
			src += "   }\n";
		}
		src += "   ;\n";

		return src;
	}
	
	private String generateWhileGrammar() {
		String src = "";
		src += "whileLoop (WhileLoop) ::= \n";
		if (whileLoop) {
			src += "   'while' '(' expression ')' '{' codeBlock '}' \n";
			src += "   {\n";
			src += "      $$ = new WhileLoop($expression, $codeBlock);\n";
			src += "   }\n";
		}
		src += "   ;\n";

		return src;
	}
	
	private String generateDoWhileGrammar() {
		String src = "";
		src += "doWhileLoop (DoWhileLoop) ::= \n";
		if (doWhileLoop) {
			src += "   'do' '{' codeBlock '}' 'while' '(' expression ')' \n";
			src += "   {\n";
			src += "      $$ = new DoWhileLoop($codeBlock, $expression);\n";
			src += "   }\n";
		}
		src += "   ;\n";

		return src;
	}
	
	private String generateForGrammar() {
		String src = "";
		src += "forLoop (ForLoop) ::= \n";
		if (forLoop) {
			src += "   'for' '(' init = statementOpt ';' expressionOpt ';' repeat = statementOpt ')' '{' codeBlock '}' \n";
			src += "   {\n";
			src += "      $$ = new ForLoop($init, $expressionOpt, $repeat, $codeBlock);\n";
			src += "   }\n";
			src += "   \n";
		}
		src += "   ;\n";
		src += "\n";
		src += "statementOpt (List<ASTNode>) ::=\n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<ASTNode>();\n";
		src += "   }\n";
		src += "   \n";
		src += "   | statement\n";
		src += "   {\n";
		src += "      $$ = $statement;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "expressionOpt (Expression) ::=\n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = null;\n";
		src += "   }\n";
		src += "   \n";
		src += "   | expression\n";
		src += "   {\n";
		src += "      $$ = $expression;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";

		return src;
	}
}