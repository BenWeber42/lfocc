package lfocc.framework.feature.service;

import lfocc.framework.feature.FrameworkInterface;

public interface ServiceProvider extends FrameworkInterface {
	public Service getService(String feature, String service);
}
