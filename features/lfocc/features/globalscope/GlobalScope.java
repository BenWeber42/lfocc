package lfocc.features.globalscope;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.Service;
import lfocc.framework.feature.service.SyntaxExtender;

public class GlobalScope extends SyntaxExtendable {
	
	@Override
	public String getName() {
		return "GlobalScope";
	}

	@Override
	public void setup(FeatureHelper helper) {
		Service service = new SyntaxExtender(this);
		helper.registerService(service);
	}


	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {

		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
		cg.getParserGenerator().setRootRule("globalScope");
	}
	
	private String generateParserSource() {
		String src = "";
		src += "globalScope : \n";
		
		Iterator<String> it = rules.iterator();
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