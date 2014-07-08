package lfocc.framework.feature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lfocc.framework.feature.service.ExtenderService;

public class MultiExtendable extends Feature {
	
	private Map<String, Set<String>> extensions = new HashMap<String, Set<String>>();
	private Map<String, ExtenderService> extenders = new HashMap<String, ExtenderService>();

	public MultiExtendable(Set<String> extenderNames) {
		for (Iterator<String> it = extenderNames.iterator(); it.hasNext();) {
			String name = it.next();
			extensions.put(name, new HashSet<String>());
			extenders.put(name, new MultiExtender(name));
		}
	}
	
	protected ExtenderService getExtender(String name) {
		return extenders.get(name);
	}
	
	protected Set<String> getExtensions(String name) {
		return extensions.get(name);
	}
	
	private class MultiExtender extends ExtenderService {
		
		private String name;
		
		public MultiExtender(String name) {
			this.name = name;
		}

		@Override
		public void addSyntaxRule(String rule) {
			extensions.get(name).add(rule);
		}

		@Override
		public String getFeature() {
			return getName();
		}
		
		@Override
		public String getServiceName() {
			return name;
		}
		
	}
}
