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

		if (ifConditional)
			codeBlock.addSyntaxRule("ifConditional");
		if (whileLoop)
			codeBlock.addSyntaxRule("whileLoop");
		if (doWhileLoop)
			codeBlock.addSyntaxRule("doWhileLoop");
		if (forLoop)
			codeBlock.addSyntaxRule("forLoop");
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		if (ifConditional) {
			cg.getParserGenerator().addParserSource(getName(), generateIfSource());
			cg.getParserGenerator().addToken("'if'", "/if/");
			if (elseConditional | elseIfConditional)
				cg.getParserGenerator().addToken("'else'", "/else/");
		}
		if (whileLoop) {
			cg.getParserGenerator().addParserSource(getName(), generateWhileSource());
			cg.getParserGenerator().addToken("'while'", "/while/");
		}
		if (doWhileLoop) {
			cg.getParserGenerator().addParserSource(getName(), generateDoWhileSource());
			cg.getParserGenerator().addToken("'do'", "/do/");
			if (!whileLoop)
				cg.getParserGenerator().addToken("'while'", "/while/");
		}
		if (forLoop) {
			cg.getParserGenerator().addParserSource(getName(), generateForGrammar());
			cg.getParserGenerator().addToken("'for'", "/for/");
		}
	}
	
	private String generateIfSource() {
		String src = "";
		src += "ifConditional ::= \n";
		if (ifConditional)
			src += "   'if' '(' expression ')' '{' codeBlock '}' ifRest ;\n";
		src += "\n";

		src += "ifRest ::= \n";
		src += "   # empty\n";
		if (elseConditional || elseIfConditional)
			src += "   | 'else' ifRest2\n";
		src += "   ;\n";
		src += "\n";
		src += "ifRest2 ::=\n";
		if (elseIfConditional) {
			src += "   'if' '(' expression ')' '{' codeBlock '}' ifRest\n";
			src += "   | '{' codeBlock '}'\n";
		} else if (elseConditional) {
			src += "   '{' codeBlock '}'\n";
		}
		src += "   ;\n";

		return src;
	}
	
	private String generateWhileSource() {
		String src = "";
		src += "whileLoop ::= \n";
		if (whileLoop)
			src += "   'while' '(' expression ')' '{' codeBlock '}' \n";
		src += "   ;\n";

		return src;
	}
	
	private String generateDoWhileSource() {
		String src = "";
		src += "doWhileLoop ::= \n";
		if (doWhileLoop)
			src += "   'do' '{' codeBlock '}' 'while' '(' expression ')' \n";
		src += "   ;\n";

		return src;
	}
	
	private String generateForGrammar() {
		String src = "";
		src += "forLoop ::= \n";
		if (forLoop)
			src += "   'for' '(' statementOpt ';' expressionOpt ';' statementOpt ')' '{' codeBlock '}' \n";
		src += "   ;\n";
		src += "\n";
		src += "statementOpt ::=\n";
		src += "   # empty\n";
		src += "   | statement\n";
		src += "   ;\n";
		src += "\n";
		src += "expressionOpt ::=\n";
		src += "   # empty\n";
		src += "   | expression\n";
		src += "   ;\n";

		return src;
	}
}