package lfocc.features.functions.services;

import lfocc.framework.feature.service.Service;

public class FunctionsConfig extends Service {
	
	private boolean hasGlobals;
	
	public FunctionsConfig(boolean hasGlobals) {
		this.hasGlobals = hasGlobals;
	}

	@Override
	public String getFeature() {
		return "Functions";
	}
	
	public boolean hasGlobals() {
		return hasGlobals;
	}

}
