package lfocc.features.globalscope.services;

import lfocc.features.globalscope.GlobalScope;
import lfocc.framework.feature.service.Service;

public class GlobalScopeService implements Service {
	
	private GlobalScope feature;
	
	public GlobalScopeService(GlobalScope feature) {
		this.feature = feature;
	}
	
	/**
	 * Adds a parser rule to the set of choices for the globalScope rule.
	 * 
	 * Note: This is in the context of the syntax stage.
	 * 
	 * Note: Every rule may appear 0 or more times in any order.
	 * If multiplicity or order is required, this should be enforced during the
	 * semantic checking stage
	 */
	public void addSyntaxRule(String rule) {
		feature.addSyntaxRule(rule);
	}

	@Override
	public String getFeature() {
		return "GlobalScope";
	}

	@Override
	public String getServiceName() {
		return "GlobalScopeManager";
	}

}
