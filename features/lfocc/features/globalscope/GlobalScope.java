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

		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());
		cg.getParserGenerator().setRootRule(
				"globalScope\n" + 
				"   {\n" + 
				"      $$ = $globalScope;\n" + 
				"   }\n"
				);
	}
	
	private String generateGrammar() {
		String src = "";
		src += "globalScope (List<ASTNode>) ::= \n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<ASTNode>();\n";
		src += "   }\n";
		src += "   \n";
		src += "   | globals = _globalScope\n";
		src += "   {\n";
		src += "      $$ = $globals;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "_globalScope (List<ASTNode>) ::= \n";
		src += "   globalScopeElement\n";
		src += "   {\n";
		src += "      $$ = $globalScopeElement;\n";
		src += "   }\n";
		src += "   \n";
		src += "   | prev = globalScopeElement next = _globalScope\n";
		src += "   {\n";
		src += "      $prev.addAll($next);\n";
		src += "      $$ = $prev;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "globalScopeElement (List<ASTNode>) ::=\n";
		
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