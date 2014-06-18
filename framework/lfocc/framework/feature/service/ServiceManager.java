package lfocc.framework.feature.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceManager {
	
	private Map<String, Map<String, Service>> services = new HashMap<String, Map<String, Service>>();

	public void addService(String feature, String name, Service service) {
		if (!services.containsKey(feature))
			services.put(feature, new HashMap<String, Service>());
		
		services.get(feature).put(name, service);
	}

	public Service getService(String feature, String service) {
		return services.get(feature) != null ? services.get(feature).get(service) : null;
	}

}
