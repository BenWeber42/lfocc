package lfocc.features.globalscope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lfocc.features.globalscope.services.GlobalScopeService;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.Service;
import lfocc.framework.feature.service.ServiceProvider;

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
	public void setup(FeatureHelper helper) {
		Service service = new GlobalScopeService(this);
		helper.registerService(service);
	}


	@Override
	public void setupFeatureArrangements(ServiceProvider provider) {}

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

		cg.getParserGenerator().addParserGrammar(getName(), grammar, "GlobalScope");
		cg.getParserGenerator().setRootRule("globalScope");
	}

}