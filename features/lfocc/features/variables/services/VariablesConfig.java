package lfocc.features.variables.services;

import lfocc.framework.feature.service.Service;

public class VariablesConfig extends Service {
	
	private boolean hasGlobals;
	private boolean hasClassMembers;

	public VariablesConfig(boolean hasGlobals, boolean hasClassMembers) {
		this.hasGlobals = hasGlobals;
		this.hasClassMembers = hasClassMembers;
	}
	
	@Override
	public String getFeature() {
		return "Variables";
	}
	
	public boolean hasGlobals() {
		return hasGlobals;
	}

	public boolean hasClassMembers() {
		return hasClassMembers;
	}

}
