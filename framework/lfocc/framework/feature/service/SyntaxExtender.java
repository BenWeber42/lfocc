package lfocc.framework.feature.service;

import lfocc.framework.feature.SyntaxExtendable;

public class SyntaxExtender extends Service {
	
	private SyntaxExtendable feature;
	
	public SyntaxExtender(SyntaxExtendable feature) {
		this.feature = feature;
	}

	@Override
	public String getFeature() {
		return feature.getName();
	}
	
	public void addSyntaxRule(String rule) {
		feature.addSyntaxRule(rule);
	}

}
