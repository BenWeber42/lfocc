package lfocc.features.functions.services;

import lfocc.features.functions.Functions;
import lfocc.framework.feature.service.Service;

public class CallExtender extends Service {

	private Functions functions;

	public CallExtender(Functions functions) {
		this.functions = functions;
	}
	
	public void addSyntaxRule(String rule) {
		functions.addCallRule(rule);
	}

	@Override
	public String getFeature() {
		return functions.getName();
	}

}
