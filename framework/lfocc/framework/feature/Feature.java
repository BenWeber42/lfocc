package lfocc.framework.feature;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.service.ServiceProvider;

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
	public String getName(); // TODO: should probably be static
	/**
	 * This should:
	 * - configure the feature (FeatureHelper.getConfiguration)
	 * - give information about the loaded configuration (FeatureHelper.printConfiguration)
	 * - declare all dependencies (FeatureHelper.depends)
	 * - register all services provided by this feature (FeatureHelper.registerService)
	 */
	public void setup(FeatureHelper helper);
	/**
	 * This should setup all inter-feature arrangements
	 * (Those will mainly work via services)
	 */
	public void setupFeatureArrangements(ServiceProvider provider);
	public void setupCompilerGenerator(CompilerGenerator cg);
}
