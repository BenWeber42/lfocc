package lfocc.features.globalscope;

import java.io.File;
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
		cg.getParserGenerator().addImport("lfocc.features.globalscope.ast.*");
		
		cg.addSource("lfocc.features.globalscope.ast",
				new File("features/lfocc/features/globalscope/ast/GlobalScope.java"));
	}
	
	private String generateGrammar() {
		String src = "";
		src += "globalScope (GlobalScope) ::= \n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new GlobalScope(new ArrayList<ASTNode>());\n";
		src += "   }\n";
		src += "   \n";
		src += "   | globals = _globalScope\n";
		src += "   {\n";
		src += "      $$ = $globals;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "_globalScope (GlobalScope) ::= \n";
		src += "   globalScopeElement\n";
		src += "   {\n";
		src += "      $$ = new GlobalScope(new ArrayList<ASTNode>(Arrays.asList($globalScopeElement)));\n";
		src += "   }\n";
		src += "   \n";
		src += "   | prev = globalScopeElement next = _globalScope\n";
		src += "   {\n";
		src += "      $next.getChildren().add($prev);\n";
		src += "      $$ = $next;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "globalScopeElement (ASTNode) ::=\n";
		
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