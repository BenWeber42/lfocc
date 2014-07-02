package lfocc.features.classes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lfocc.features.globalscope.services.GlobalScopeService;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.service.ServiceManager;

public class Classes implements Feature {
	
	private Set<String> rules = new HashSet<String>();

	@Override
	public String getName() {
		return "Classes";
	}

	@Override
	public void configure(File config) {
	}

	@Override
	public void setup() {
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

	@Override
	public List<String> getConfiguration() {
		return null;
	}

	@Override
	public Set<String> getDependencies() {
		return new HashSet<String>(Arrays.asList(
				"GlobalScope",
				"SyntaxBase"));
	}

	@Override
	public void registerServices(ServiceManager serviceManager) {
	}

	@Override
	public void setupFeatureArrangements(ServiceManager serviceManager) {
		GlobalScopeService globalScope = (GlobalScopeService)
				serviceManager.getService("GlobalScope", "GlobalScopeManager");
		globalScope.addSyntaxRule("ClassDecl");

	}
	
	public void addSyntaxRule(String rule) {
		rules.add(rule);
	}

}
