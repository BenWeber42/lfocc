package lfocc.features.variables.services;

import lfocc.framework.feature.service.Service;

public class VariablesConfig extends Service {
	
	private boolean hasGlobals;

	public VariablesConfig(boolean hasGlobals) {
		this.hasGlobals = hasGlobals;
	}
	
	@Override
	public String getFeature() {
		return "Variables";
	}
	
	public boolean hasGlobals() {
		return hasGlobals;
	}

}
