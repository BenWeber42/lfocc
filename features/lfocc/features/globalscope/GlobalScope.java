package lfocc.features.globalscope;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lfocc.features.globalscope.services.GlobalScopeService;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.service.Service;
import lfocc.framework.feature.service.ServiceManager;

public class GlobalScope implements Feature {
	
	List<String> choice = new ArrayList<String>();
	
	public void addSyntaxRule(String rule) {
		choice.add(rule);
	}

	@Override
	public String getName() {
		return "GlobalScope";
	}

	@Override
	public void configure(File config) {}

	@Override
	public void setup() {}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		String grammar = 
				"parser grammar GlobalScope;\n" +
				"\n" + 
				"globalScope : (";
		
		Iterator<String> it = choice.iterator();
		if (it.hasNext()) {
			grammar += " " + it.next();
		}
		while (it.hasNext()) {
			grammar += "\n   | " + it.next();
		}
		
		grammar += " )* EOF;";

		cg.getParserGenerator().addGrammar(getName(), grammar, "GlobalScope");
		cg.getParserGenerator().setRootRule("globalScope");
	}

	@Override
	public List<String> getConfiguration() {
		return null;
	}

	@Override
	public Set<String> getDependencies() {
		return null;
	}

	@Override
	public void registerServices(ServiceManager serviceManager) {
		Service service = new GlobalScopeService(this);
		serviceManager.addService(getName(), service.getServiceName(), service);
	}

	@Override
	public void setupFeatureArrangements(ServiceManager serviceManager) {}

}
