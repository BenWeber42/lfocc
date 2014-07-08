package lfocc.features.globalscope;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;

public class GlobalScope extends SingleExtendable {
	
	@Override
	public String getName() {
		return "GlobalScope";
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender());
	}


	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {

		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
		cg.getParserGenerator().setRootRule("globalScope");
	}
	
	private String generateParserSource() {
		String src = "";
		src += "globalScope : \n";
		
		Iterator<String> it = extensions.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";

			while (it.hasNext())
				src += "   | " + it.next() + "\n";

			src += "   )*\n";
		}
		
		src += "   EOF ;\n";	
		return src;
	}

}