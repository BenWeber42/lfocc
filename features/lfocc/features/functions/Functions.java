package lfocc.features.functions;

import java.io.File;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.Grammar;
import lfocc.framework.compilergenerator.parsergenerator.FileGrammar;
import lfocc.framework.compilergenerator.parsergenerator.StringGrammar;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class Functions extends Feature {
	
	public final static String FUNCTIONS_GRAMMAR_FILE =
			"features/lfocc/features/functions/Functions.g";
	public final static String FUNCTIONS_GRAMMAR_NAME = "Functions";

	// TODO: add configurability
	private boolean global = true; // function declarations in global scope
	private boolean classes = true; // function declarations as class members
	private boolean returnExpression = true; // return value allowed
	private boolean statement = true; // function call as statement
	private boolean expression = true; // function call as expression

	public void setup(FeatureHelper helper) {
		
		if (!global && !classes)
			return; // function declarations can't occur anywhere, nothing to do

		helper.depends("CodeBlock");
		
		if (global)
			helper.depends("GlobalScope");
		if (classes)
			helper.depends("Classes");
		if (expression || returnExpression)
			helper.depends("Expressions");
		if (statement)
			helper.depends("Statement");
		
	}
	
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (!global && !classes)
			return;

		SyntaxExtender extender = (SyntaxExtender) 
				services.getService("CodeBlock", "SyntaxExtender");
		//extender.addSyntaxRule("returnStmt");
		
		if (global) {
			extender = (SyntaxExtender) 
					services.getService("GlobalScope", "SyntaxExtender");
			extender.addSyntaxRule("functionDeclaration");
		}
		if (classes) {
			extender = (SyntaxExtender) 
					services.getService("Classes", "SyntaxExtender");
			extender.addSyntaxRule("functionDeclaration");
		}
		if (statement) {
			extender = (SyntaxExtender) 
					services.getService("Statement", "SyntaxExtender");
			extender.addSyntaxRule("functionCall");
		}
		if (expression) {
			extender = (SyntaxExtender) 
					services.getService("Expressions", "SyntaxExtender");
			extender.addSyntaxRule("functionCall");
		}
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {

		if (!global && !classes)
			return;

		cg.getParserGenerator().addParserGrammar(generateFunctionGrammar());
		//cg.getParserGenerator().addParserGrammar(generateReturnGrammar());
	}
	
	public Grammar generateFunctionGrammar() {
		return new FileGrammar(getName(), new File(FUNCTIONS_GRAMMAR_FILE),
				FUNCTIONS_GRAMMAR_NAME);
	}
	
	public Grammar generateReturnGrammar() {
		String name = "ReturnStmt";
		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		if (returnExpression)
			src += "returnStmt : 'return' ( expression )? ';' ;";
		else
			src += "returnStmt : 'return' ';' ;";
		
		return new StringGrammar(getName(), src, name);
	}
}
