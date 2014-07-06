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
		src += "\n";
		src += "Identifier : ( 'a'..'z' | 'A'..'Z')\n";
		src += "   ('a'..'z' | 'A'..'Z' | '0'..'9')*\n";
		src += "   ;\n";
		src += "\n";
		src += "Integer : (\n";
		src += "   ('1'..'9') ('0'..'9')*\n";
		src += "   | '0x' ('0'..'9' | 'a'..'f' | 'A'..'F' )*\n";
		src += "   );\n";
		src += "\n";
		src += "Float : ('1'..'9') ('0'..'9')* '.' ('0'..'9')*;\n";
		return src;
	}

}
