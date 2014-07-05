package lfocc.features.controlflow;

import java.io.File;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.Grammar;
import lfocc.framework.compilergenerator.parsergenerator.StringGrammar;
import lfocc.framework.compilergenerator.parsergenerator.FileGrammar;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class ControlFlow extends Feature {
	
	public static final String CONTROL_FLOW_WHILE_FILE =
			"features/lfocc/features/controlflow/WhileLoop.g";
	public static final String CONTROL_FLOW_WHILE_NAME = "WhileLoop";

	public static final String CONTROL_FLOW_DO_WHILE_FILE =
			"features/lfocc/features/controlflow/DoWhileLoop.g";
	public static final String CONTROL_FLOW_DO_WHILE_NAME = "DoWhileLoop";

	public static final String CONTROL_FLOW_FOR_FILE =
			"features/lfocc/features/controlflow/ForLoop.g";
	public static final String CONTROL_FLOW_FOR_NAME = "ForLoop";

	// TODO: add configurability for the different types:
	private boolean ifConditional = true;
	private boolean elseConditional = true;
	private boolean elseIfConditional = true;
	private boolean whileLoop = true;
	private boolean doWhileLoop = true;
	private boolean forLoop = false;
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("CodeBlock");
		helper.depends("Expressions");
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
		src += "ifConditional : \n";
		if (ifConditional)
			src += "   'if' '(' expression ')' '{' codeBlock '}' \n";
		if (elseIfConditional)
			src += "   'else' 'if' '(' expression ')' '{' codeBlock '}' \n";
		if (elseConditional)
			src += "   'else' '{' codeBlock '}' \n";
		src += "   ;\n";

		
		return new StringGrammar(getName(), src, name);
	}
	
	private Grammar generateWhileGrammar() {
		return new FileGrammar(getName(), new File(CONTROL_FLOW_WHILE_FILE),
				CONTROL_FLOW_WHILE_NAME);
	}
	
	private Grammar generateDoWhileGrammar() {
		return new FileGrammar(getName(), new File(CONTROL_FLOW_DO_WHILE_FILE),
				CONTROL_FLOW_DO_WHILE_NAME);
	}
	
	private Grammar generateForGrammar() {
		return new FileGrammar(getName(), new File(CONTROL_FLOW_FOR_FILE),
				CONTROL_FLOW_FOR_NAME);
	}
}
