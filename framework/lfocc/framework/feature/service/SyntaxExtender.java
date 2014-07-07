package lfocc.framework.feature.service;

import lfocc.framework.feature.SyntaxExtendable;

public class SyntaxExtender extends Service {
	
	private SyntaxExtendable feature;
	private String name = null;
	
	public SyntaxExtender(SyntaxExtendable feature) {
		this.feature = feature;
	}

	public SyntaxExtender(SyntaxExtendable feature, String name) {
		this.feature = feature;
		this.name = name;
	}

	@Override
	public String getFeature() {
		return feature.getName();
	}
	
	@Override
	public String getServiceName() {
		if (name != null)
			return name;
		return super.getServiceName();
	}

	public void addSyntaxRule(String rule) {
		feature.addSyntaxRule(rule);
	}

}
