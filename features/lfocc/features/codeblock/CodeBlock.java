package lfocc.features.codeblock;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.SyntaxExtender;

public class CodeBlock extends SyntaxExtendable {
	
	public void setup(FeatureHelper helper) {
		helper.registerService(new SyntaxExtender(this));
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateParser());
	}
	
	public String generateParser() {
		String src = "";
		
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
