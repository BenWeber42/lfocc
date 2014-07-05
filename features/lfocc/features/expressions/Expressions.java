package lfocc.features.expressions;

import java.io.File;
import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.FileGrammar;
import lfocc.framework.compilergenerator.parsergenerator.Grammar;
import lfocc.framework.compilergenerator.parsergenerator.StringGrammar;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.SyntaxExtender;

public class Expressions extends SyntaxExtendable {
	
	public final static String EXPRESSIONS_GRAMMAR_FILE = 
			"features/lfocc/features/expressions/Expressions.g";
	public final static String EXPRESSIONS_GRAMMAR_NAME = "Expressions";
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(new SyntaxExtender(this));
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserGrammar(generateExternalGrammar());
		cg.getParserGenerator().addParserGrammar(new FileGrammar(
				getName(), new File(EXPRESSIONS_GRAMMAR_FILE), EXPRESSIONS_GRAMMAR_NAME));
	}
	
	private Grammar generateExternalGrammar() {
		String name = "externalExpressions";
		String src = "";
		src += "parser grammar " + name + ";\n";
		src += "\n";
		src += "externalExpression :\n";
		
		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";
			while (it.hasNext()) {
				src += "   | " + it.next() + "\n";
			}
			src += "   )*\n";
		}
		src += "   ;\n";
		
		return new StringGrammar(getName(), src, name);
	}
}
