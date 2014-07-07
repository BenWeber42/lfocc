package lfocc.features.functions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lfocc.features.functions.services.CallExtender;
import lfocc.features.functions.services.DeclarationExtender;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class Functions extends Feature {
	
	// TODO: add configurability
	private boolean global = true; // function declarations in global scope
	private boolean classes = true; // function declarations as class members
	private boolean returnExpression = true; // return value allowed
	private boolean statement = true; // function call as statement
	private boolean expression = true; // function call as expression
	
	private Set<String> declarationRules = new HashSet<String>();
	private Set<String> callRules = new HashSet<String>();
	
	public void addDeclarationRule(String rule) {
		declarationRules.add(rule);
	}

	public void addCallRule(String rule) {
		callRules.add(rule);
	}

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
		
		helper.registerService(new DeclarationExtender(this));
		helper.registerService(new CallExtender(this));
		
	}
	
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (!global && !classes)
			return;

		SyntaxExtender extender = (SyntaxExtender) 
				services.getService("CodeBlock", "SyntaxExtender");
		extender.addSyntaxRule("returnStmt");
		
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

		cg.getParserGenerator().addParserSource(getName(), generateFunctionGrammar());
		cg.getParserGenerator().addParserSource(getName(), generateReturnGrammar());
	}
	
	public String generateFunctionGrammar() {
		String src = "";
		src += "functionDeclaration :\n";
		src += "   Identifier Identifier";
		src += "   '('\n";
		
		Iterator<String> it = declarationRules.iterator();
		if (it.hasNext()) {
			src += "      (\n";
			src += "      " + it.next() + "\n";
			
			while (it.hasNext())
				src += "      | " + it.next() + "\n";

			src += "      )?\n";
		}
		
		src += "   ')'\n";
		src += "   '{' codeBlock '}'\n";
		src += "   ;\n";
		src += "\n";
		src += "functionCall :\n";
		src += "   Identifier\n";
		src += "   '('\n";

		it = callRules.iterator();
		if (it.hasNext()) {
			src += "      (\n";
			src += "      " + it.next() + "\n";
			
			while (it.hasNext())
				src += "      | " + it.next() + "\n";

			src += "      )?\n";
		}
	
		src += "   ')'\n";
		src += "   ;\n";
		
		return src;
	}
	
	public String generateReturnGrammar() {
		String src = "";
		if (returnExpression)
			src += "returnStmt : 'return' ( expression )? ';' ;\n";
		else
			src += "returnStmt : 'return' ';' ;\n";
		
		return src;
	}
}
