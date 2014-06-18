package lfocc.framework.feature;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FeatureLoader {
	// features that have been loaded so far
	private Map<String, Feature> features = new HashMap<String, Feature>();
	// path to look for features
	private String path;
	
	/**
	 * @param path where to look for features
	 */
	public FeatureLoader(String path) {
		this.path = path;
	}
	
	/**
	 * Loads a single feature `name`.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void load(String name) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (features.containsKey(name))
			return;
		
		File basePath = new File(path);
		
		ClassLoader classLoader;
		try {
			classLoader = new URLClassLoader(new URL[]{basePath.toURL()});
			Class<?> clazz = classLoader.loadClass("lfocc.features." + name.toLowerCase() + "." + name);
			Object feature = clazz.newInstance();
			if (feature instanceof Feature) {
				features.put(name,  (Feature) feature);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads a whole set of features.
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void load(Set<String> features) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Iterator<String> it = features.iterator();
		while (it.hasNext()) {
			load(it.next());
		}
	}
	
	/**
	 * Gets the feature class `name`.
	 * 
	 * Warning: The feature must have been successfully loaded before!
	 */
	public Feature getFeature(String name) {
		return features.get(name);
	}
	
	/**
	 * Gets all feature classes.
	 */
	public Map<String, Feature> getAllFeatures() {
		return new HashMap<String, Feature>(features);
	}
}
