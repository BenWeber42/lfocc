package lfocc.features.types;

import java.io.File;
import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;

public class Types extends SingleExtendable {

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("Base");
		helper.registerService(getExtender());
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());
		cg.getParserGenerator().addImport("lfocc.features.types.ast.TypeSymbol");
		cg.addSource("lfocc.features.types.ast",
				new File("features/lfocc/features/types/ast/TypeSymbol.java"));
	}
	
	private String generateGrammar() {
		String src = "";
		src += "type (TypeSymbol) ::= \n";
		Iterator<String> it = extensions.iterator();
		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
			while (it.hasNext()) {
				src += "   | " + it.next() + "\n";
			}
		}
		src += "   ;\n";
		
		return src;
	}
}
