package lfocc.features.controlflow;

import lfocc.framework.compilergenerator.CompilerGenerator;
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
		helper.depends("Expressions");
		
		if (forLoop)
			helper.depends("Statement");
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
			cg.getParserGenerator().addParserSource(getName(), generateIfSource());
		if (whileLoop)
			cg.getParserGenerator().addParserSource(getName(), generateWhileSource());
		if (doWhileLoop)
			cg.getParserGenerator().addParserSource(getName(), generateDoWhileSource());
		if (forLoop)
			cg.getParserGenerator().addParserSource(getName(), generateForGrammar());
	}
	
	private String generateIfSource() {
		String src = "";
		src += "ifConditional : \n";
		if (ifConditional)
			src += "   'if' '(' expression ')' '{' codeBlock '}' \n";
		if (elseIfConditional)
			src += "   'else' 'if' '(' expression ')' '{' codeBlock '}' \n";
		if (elseConditional)
			src += "   'else' '{' codeBlock '}' \n";
		src += "   ;\n";

		return src;
	}
	
	private String generateWhileSource() {
		String src = "";
		src += "whileLoop : \n";
		if (whileLoop)
			src += "   'while' '(' expression ')' '{' codeBlock '}' \n";
		src += "   ;\n";

		return src;
	}
	
	private String generateDoWhileSource() {
		String src = "";
		src += "doWhileLoop : \n";
		if (doWhileLoop)
			src += "   'do' '{' codeBlock '}' 'while' '(' expression ')' \n";
		src += "   ;\n";

		return src;
	}
	
	private String generateForGrammar() {
		String src = "";
		src += "forLoop : \n";
		if (forLoop)
			src += "   'for' '(' statement ';' expression ';' statement ')' '{' codeBlock '}' \n";
		src += "   ;\n";

		return src;
	}
}