package lfocc.features.expressions;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.MultiExtendable;

public class Expressions extends MultiExtendable {
	
	private static final String expressionExtender = "ExpressionExtender";
	private static final String assignableExpressionExtender = "AssignableExpressionExtender";

	public Expressions() {
		super(new HashSet<String>(Arrays.asList(
				expressionExtender, assignableExpressionExtender
				)));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender(expressionExtender));
		helper.registerService(getExtender(assignableExpressionExtender));
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateSource());
		cg.getParserGenerator().addToken("'+'", "/\\+/");
		cg.getParserGenerator().addToken("'-'", "/\\-/");
		cg.getParserGenerator().addToken("'*'", "/\\*/");
		cg.getParserGenerator().addToken("'/'", "/\\//");
		cg.getParserGenerator().addToken("'%'", "/%/");
		cg.getParserGenerator().addToken("'&&'", "/&&/");
		cg.getParserGenerator().addToken("'||'", "/\\|\\|/");
		cg.getParserGenerator().addToken("'!'", "/!/");
		cg.getParserGenerator().addToken("'=='", "/==/");
		cg.getParserGenerator().addToken("'!='", "/!=/");
		cg.getParserGenerator().addToken("'<='", "/<=/");
		cg.getParserGenerator().addToken("'<'", "/</");
		cg.getParserGenerator().addToken("'>='", "/>=/");
		cg.getParserGenerator().addToken("'>'", "/>/");
		cg.getParserGenerator().addPrecedence("'==' '!=' '<=' '>=' '<' '>'", 0);
		cg.getParserGenerator().addPrecedence("'+' '-' '||'", 1);
		cg.getParserGenerator().addPrecedence("'*' '/' '%' '&&'", 2);
		cg.getParserGenerator().addPrecedence("'!'", 3);

		cg.getParserGenerator().addToken("integer", "/0|[1-9][0-9]*|0x[a-fA-F0-9]+/");
		cg.getParserGenerator().addToken("float", "/(0|[1-9][0-9]*)\\.[0-9]+/");
		cg.getParserGenerator().addToken("boolean", "/true|false/");
	}

	private String generateSource() {
		String src = "";
		
		src += "expression ::=\n";
		src += "   '(' expression ')'\n";
		src += "   | integer\n";
		src += "   | float\n";
		src += "   | boolean\n";
		src += "   | expression '+' expression\n";
		src += "   | expression '-' expression\n";
		src += "   | expression '*' expression\n";
		src += "   | expression '/' expression\n";
		src += "   | expression '%' expression\n";
		src += "   | expression '&&' expression\n";
		src += "   | expression '||' expression\n";
		src += "   | '!' expression\n";
		src += "   | expression '==' expression\n";
		src += "   | expression '!=' expression\n";
		src += "   | expression '<=' expression\n";
		src += "   | expression '<' expression\n";
		src += "   | expression '>=' expression\n";
		src += "   | expression '>' expression\n";
		
		Iterator<String> it = getExtensions(expressionExtender).iterator();
		while (it.hasNext())
			src += "   | " + it.next() + "\n";
		src += "   ;\n";
		src += "\n";
		src += "assignableExpression ::=\n";
		it = getExtensions(assignableExpressionExtender).iterator();

		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			while (it.hasNext())
				src += "   | " + it.next() + "\n";
		}
		src += "   ;\n";
		return src;
	}
}
