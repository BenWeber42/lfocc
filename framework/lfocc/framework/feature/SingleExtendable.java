package lfocc.framework.feature;

import java.util.HashSet;
import java.util.Set;

import lfocc.framework.feature.service.ExtenderService;

public class SingleExtendable extends Feature {

	protected Set<String> extensions = new HashSet<String>();
	private SingleExtender extender = new SingleExtender();
	
	protected ExtenderService getExtender() {
		return extender;
	}
	
	private class SingleExtender extends ExtenderService {

		@Override
		public void addSyntaxRule(String rule) {
			extensions.add(rule);
		}

		@Override
		public String getFeature() {
			return getName();
		}
		
		@Override
		public String getServiceName() {
			return "Extender";
		}
		
	}
}
