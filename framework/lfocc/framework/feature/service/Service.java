package lfocc.framework.feature.service;

public abstract class Service {
	
	public abstract String getFeature();
	public String getServiceName() {
		return this.getClass().getSimpleName();
	}
}