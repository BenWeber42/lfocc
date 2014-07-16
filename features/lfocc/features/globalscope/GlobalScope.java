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
		// TODO: EOF
		src += "globalScope ::= \n";
		src += "   # empty\n";
		src += "   | _globalScope\n";
		src += "   ;\n";
		src += "\n";
		src += "_globalScope ::= \n";
		src += "   globalScopeElement\n";
		src += "   | globalScopeElement _globalScope\n";
		src += "   ;\n";
		src += "\n";
		src += "globalScopeElement ::=\n";
		src += "\n";
		src += "\n";
		
		Iterator<String> it = extensions.iterator();
		if (it.hasNext()) {
			src += "   " + it.next() + "\n";

			while (it.hasNext())
				src += "   | " + it.next() + "\n";

		}
		
		src += "   ;\n";
		src += "\n";

		return src;
	}

}