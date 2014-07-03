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
		String grammar = 
				"parser grammar GlobalScope;\n" +
				"\n" + 
				"globalScope : \n";
		
		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			grammar += "   (\n";
			grammar += "   " + it.next() + "\n";

			while (it.hasNext())
				grammar += "   | " + it.next() + "\n";

			grammar += "   )*\n";
		}
		
		grammar += "   EOF ;";

		cg.getParserGenerator().addParserGrammar(getName(), grammar, "GlobalScope");
		cg.getParserGenerator().setRootRule("globalScope");
	}

}