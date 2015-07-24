package lfocc.features.codeblock;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;

public class CodeBlock extends SingleExtendable {
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender());
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());
	}
	
	private String generateGrammar() {
		String src = "";
		
		src += "codeBlock (List<ASTNode>) ::= \n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<ASTNode>();\n";
		src += "   }\n";
		src += "   \n";
		src += "   | _codeBlock\n";
		src += "   {\n";
		src += "      $$ = $_codeBlock;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "_codeBlock (List<ASTNode>) ::= \n";
		src += "   codeBlockElement\n";
		src += "   {\n";
		src += "      $$ = $codeBlockElement;\n";
		src += "   }\n";
		src += "   \n";
		src += "   | codeBlockElement code = _codeBlock\n";
		src += "   {\n";
		src += "      $codeBlockElement.addAll($code);\n";
		src += "      $$ = $codeBlockElement;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "codeBlockElement (List<ASTNode>) ::= \n";

		Iterator<String> it = extensions.iterator();
		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			
			while (it.hasNext())
				src += "   | " + it.next() + "\n";
		}
		src += "   ;\n";
		
		return src;
	}
}
