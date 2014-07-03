package lfocc.features.functions;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.Grammar;
import lfocc.framework.compilergenerator.parsergenerator.StringGrammar;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class Functions extends Feature {

	// TODO: add configurability
	private boolean global = true;
	private boolean classes = true;

	public void setup(FeatureHelper helper) {
		
		if (!global && !classes)
			return; // functions can't occur anywhere, nothing to do

		helper.depends("CodeBlock");
		
		if (global)
			helper.depends("GlobalScope");
		if (classes)
			helper.depends("Classes");
	}
	
	public void setupFeatureArrangements(ServiceProvider services) {
		if (global) {
			SyntaxExtender extender = (SyntaxExtender) 
					services.getService("GlobalScope", "SyntaxExtender");
			extender.addSyntaxRule("function");
		}
		if (classes) {
			SyntaxExtender extender = (SyntaxExtender) 
					services.getService("Classes", "SyntaxExtender");
			extender.addSyntaxRule("function");
		}
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserGrammar(generateFunctionGrammar());
	}
	
	public Grammar generateFunctionGrammar() {
		String name = "Functions";
		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		src += "function :\n";
		// TODO: function parameters
		src += "   Identifier Identifier '(' ')'\n";
		src += "   '{' codeBlock '}'\n";
		src += "   ;\n";
		
		return new StringGrammar(getName(), src, name);
	}
}
