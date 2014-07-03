package lfocc.features.controlflow;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.Grammar;
import lfocc.framework.compilergenerator.parsergenerator.StringGrammar;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class ControlFlow extends Feature {
	// TODO: add configurability for the different types:
	private boolean ifConditional = true;
	private boolean elseConditional = true;
	private boolean elseIfConditional = true;
	private boolean whileLoop = true;
	private boolean doWhileLoop = true;
	private boolean forLoop = true;
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("CodeBlock");
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		SyntaxExtender codeBlock = (SyntaxExtender) services.getService(
				"CodeBlock", "SyntaxExtender");

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
		if (ifConditional)
			cg.getParserGenerator().addParserGrammar(generateIfGrammar());
		if (whileLoop)
			cg.getParserGenerator().addParserGrammar(generateWhileGrammar());
		if (doWhileLoop)
			cg.getParserGenerator().addParserGrammar(generateDoWhileGrammar());
		if (forLoop)
			cg.getParserGenerator().addParserGrammar(generateForGrammar());
	}
	
	private Grammar generateIfGrammar() {
		String name = "IfConditional";
		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		// TODO: boolean expression
		src += "ifConditional : \n";
		if (ifConditional)
			src += "   'if' '(' ')' '{' codeBlock '}' \n";
		if (elseIfConditional)
			src += "   'else' 'if' '(' ')' '{' codeBlock '}' \n";
		if (elseConditional)
			src += "   'else' '{' codeBlock '}' \n";
		src += "   ;\n";

		
		return new StringGrammar(getName(), src, name);
	}
	
	private Grammar generateWhileGrammar() {
		String name = "WhileLoop";

		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		// TODO: boolean expression
		src += "whileLoop : \n";
		if (whileLoop)
			src += "   'while' '(' ')' '{' codeBlock '}' \n";
		src += "   ;\n";

		return new StringGrammar(getName(), src, name);
	}
	
	private Grammar generateDoWhileGrammar() {
		String name = "DoWhileLoop";

		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		// TODO: boolean expression
		src += "doWhileLoop : \n";
		if (doWhileLoop)
			src += "   'do' '{' codeBlock '}' 'while' '(' ')' \n";
		src += "   ;\n";

		return new StringGrammar(getName(), src, name);
	}
	
	private Grammar generateForGrammar() {
		String name = "ForLoop";

		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		// TODO: boolean expression
		src += "forLoop : \n";
		if (forLoop)
			src += "   'for' '(' ';' ';' ')' '{' codeBlock '}' \n";
		src += "   ;\n";

		return new StringGrammar(getName(), src, name);
	}
	
}
