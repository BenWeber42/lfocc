package lfocc.framework.feature;

import java.io.File;
import java.util.List;
import java.util.Set;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.service.ServiceManager;

/**
 * This is the interface that every feature has to expose to the framework</br>
 * </br>
 * @author ben</br>
 * </br>
 * Feature loading & setup works as follows:</br>
 * </br>
 * 1. The framework loads a given feature.</br>
 * 2. The framework gives the config to the feature (`configure`).</br>
 * </br>
 * 3. The framework asks the feature to setup itself (`setup`).</br>
 * At this point every feature must know its dependencies!</br>
 * (Every feature must be able to setup itself without relying on other features)</br>
 * </br>
 * 4. The framework asks every feature about its dependencies and checks whether
 * they're satisfied.</br>
 * </br>
 * 5. The framework asks every feature to setup the framework. At this point it's ok</br>
 * for features to rely on other features</br>
 * </br>
 * Warning: Every feature must have a constructor without parameters!</br>
 */
public interface Feature {
	/**
	 * @return name of the feature
	 */
	public String getName();
	/**
	 * Loads the given config
	 */
	public void configure(File config);
	/**
	 * A set of Strings describing the configuration of the feature,
	 * so that the user can check that the features was configured correctly.
	 */
	public List<String> getConfiguration();
	public void setup();
	public Set<String> getDependencies();
	public void registerServices(ServiceManager serviceManager);
	public void setupFeatureArrangements(ServiceManager serviceManager);
	public void setupCompilerGenerator(CompilerGenerator cg);
}
