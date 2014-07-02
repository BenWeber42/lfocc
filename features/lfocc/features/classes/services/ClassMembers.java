package lfocc.features.classes.services;

import lfocc.features.classes.Classes;
import lfocc.framework.feature.service.Service;

public class ClassMembers implements Service {
	
	private Classes classes;
	
	public ClassMembers(Classes feature) {
		classes = feature;
	}

	@Override
	public String getFeature() {
		return classes.getName();
	}

	@Override
	public String getServiceName() {
		return "ClassMembers";
	}
	
	public void addSyntaxRule(String rule) {
		classes.addSyntaxRule(rule);
	}

}
