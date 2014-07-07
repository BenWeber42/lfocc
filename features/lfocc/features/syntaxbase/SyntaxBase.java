package lfocc.features.syntaxbase;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;

public class SyntaxBase extends Feature {
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
	}
	
	private String generateParserSource() {
		String src = "";
		src += "Identifier : ( 'a'..'z' | 'A'..'Z')\n";
		src += "   ('a'..'z' | 'A'..'Z' | '0'..'9')*\n";
		src += "   ;\n";
		return src;
	}

}
