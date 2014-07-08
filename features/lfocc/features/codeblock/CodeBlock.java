package lfocc.features.codeblock;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;

public class CodeBlock extends SingleExtendable {
	
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender());
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateParser());
	}
	
	public String generateParser() {
		String src = "";
		
		src += "codeBlock : \n";

		Iterator<String> it = extensions.iterator();
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
