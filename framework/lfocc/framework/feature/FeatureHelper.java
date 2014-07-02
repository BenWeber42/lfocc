package lfocc.framework.feature;

import java.io.File;
import java.util.List;
import java.util.Set;

import lfocc.framework.feature.service.Service;

public interface FeatureHelper {

	public File getConfiguration();
	public void printConfiguration(List<String> cfgs);
	public void depends(String feature);
	public void depends(Set<String> features);
	public void registerService(Service service);
}