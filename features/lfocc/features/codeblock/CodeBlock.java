package lfocc.features.codeblock;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.SyntaxExtender;

public class CodeBlock extends SyntaxExtendable {
	
	public static final String CODE_BLOCK_GRAMMAR_NAME = "CodeBlock";

	public void setup(FeatureHelper helper) {
		helper.registerService(new SyntaxExtender(this));
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserGrammar(
				getName(), generateParser(), CODE_BLOCK_GRAMMAR_NAME);
	}
	
	public String generateParser() {
		String src = "";
		
		src += "parser grammar " + CODE_BLOCK_GRAMMAR_NAME + ";\n";
		src += "\n";
		src += "codeBlock : \n";

		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";
			
			while (it.hasNext())
				src += "   | " + it.next() + "\n";

			src += "   )*\n";
		}
		src += "   ;\n";
		
		return src;
	}
}
