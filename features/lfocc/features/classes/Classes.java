package lfocc.features.classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lfocc.features.globalscope.services.GlobalScopeService;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ServiceProvider;

public class Classes extends Feature {
	
	private Set<String> rules = new HashSet<String>();

	@Override
	public String getName() {
		return "Classes";
	}

	public Set<String> getDependencies() {
		return new HashSet<String>(Arrays.asList(
				"GlobalScope",
				"SyntaxBase"));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends(getDependencies());
	}


	@Override
	public void setupFeatureArrangements(ServiceProvider serviceManager) {
		GlobalScopeService globalScope = (GlobalScopeService)
				serviceManager.getService("GlobalScope", "GlobalScopeManager");
		globalScope.addSyntaxRule("ClassDecl");

	}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		String source = "";
		source += "parser grammar ClassesParser;\n";
		source += "\n";
		source += "tokens {\n";
		source += "   Class;\n";
		source += "}\n";
		source += "\n";
		source += "classDecl : 'class' Identifier '{' \n";
		
		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			source += "   (\n";
			source += "      " + it.next() + "\n";
			while (it.hasNext())
				source += "      | " + it.next() + "\n";
			source += "   )*\n";
		}
		
		source += "   '}' ;\n";
		cg.getParserGenerator().addParserGrammar(getName(), source, "ClassesParser");
		
	}
	
	public void addSyntaxRule(String rule) {
		rules.add(rule);
	}

}
