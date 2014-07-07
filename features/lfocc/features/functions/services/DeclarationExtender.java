package lfocc.features.functions.services;

import lfocc.features.functions.Functions;
import lfocc.framework.feature.service.Service;

public class DeclarationExtender extends Service {

	private Functions functions;

	public DeclarationExtender(Functions functions) {
		this.functions = functions;
	}
	
	public void addSyntaxRule(String rule) {
		functions.addDeclarationRule(rule);
	}

	@Override
	public String getFeature() {
		return functions.getName();
	}
	
}
