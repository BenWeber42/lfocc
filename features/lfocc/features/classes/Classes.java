package lfocc.features.classes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lfocc.features.globalscope.services.GlobalScopeService;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.service.ServiceManager;

public class Classes implements Feature {

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
		cg.getParserGenerator().addGrammar(getName(),
				"lexer grammar Classes;\n" +
				"\n" +
				"ClassDecl : 'class';", "Classes");
	}

	@Override
	public List<String> getConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getDependencies() {
		return new HashSet<String>(Arrays.asList("GlobalScope"));
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

}
