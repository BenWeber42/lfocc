package lfocc.features.expressions;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
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
		cg.getParserGenerator().addParserSource(getName(), generateExternalSource());
		cg.getParserGenerator().addParserSource(getName(), generateInternalSource());
	}
	
	private String generateExternalSource() {
		String src = "";
		src += "externalExpression :\n";
		
		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";
			while (it.hasNext()) {
				src += "   | " + it.next() + "\n";
			}
			src += "   )\n";
		}
		src += "   ;\n";
		
		return src;
	}
	
	private String generateInternalSource() {
		String src = "";
		
		src += "expression :\n";
		src += "	internalExpression\n";
		src += "	| '(' expression ')'\n";
		src += "	;\n";
		src += "\n";
		src += "internalExpression :\n";
		src += "	comparison\n";
		src += "	;\n";
		src += "\n";
		src += "comparison :\n";
		src += "	addition (\n";
		src += "	( '==' | '!=' | '<' | '<=' | '>' | '>='	)\n";
		src += "	addition )?\n";
		src += "	;\n";
		src += "\n";
		src += "addition :\n";
		src += "	multiplication (\n";
		src += "	( '+' | '-' | '||' )\n";
		src += "	multiplication )?\n";
		src += "	;\n";
		src += "\n";
		src += "multiplication :\n";
		src += "	unsigned (\n";
		src += "	( '*' | '/' | '%' | '&&' )\n";
		src += "	unsigned )?\n";
		src += "	;\n";
		src += "\n";
		src += "unsigned :\n";
		src += "	( '+' | '-' )? externalExpression\n";
		src += "	;\n";
		
		return src;
	}
}
