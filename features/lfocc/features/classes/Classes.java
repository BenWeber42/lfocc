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
		String source = "";
		source += "parser grammar ClassesParser;\n";
		source += "\n";
		source += "tokens {\n";
		source += "   Class;\n";
		source += "}\n";
		source += "\n";
		source += "classDecl : ClassKeyWord -> Class ;";
		cg.getParserGenerator().addParserGrammar(getName(), source, "ClassesParser");
		
		source = "";
		source += "lexer grammar ClassesLexer;\n";
		source += "\n";
		source += "ClassKeyWord : 'class' ;";

		cg.getParserGenerator().addParserGrammar(getName(), source, "ClassesLexer");
	}

	@Override
	public List<String> getConfiguration() {
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
