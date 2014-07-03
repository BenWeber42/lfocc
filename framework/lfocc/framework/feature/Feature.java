package lfocc.framework.feature;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.service.ServiceProvider;

/**
 * This is the interface that every feature has to expose to the framework</br>
 * </br>
 * Feature loading & setup works as follows:</br>
 * </br>
 * 1. The framework loads a given feature.</br>
 * 2. The framework asks the feature to setup itself (`setup`).</br>
 * 3. The framework asks the feature to setup inter-feature arrangements (`setupFeatureArrangements`)</br>
 * 4. The framework asks the feature to setup the compiler generator (`setupCompilerGenerator`)</br>
 * </br>
 * Warning: Every feature must have a constructor without parameters!</br>
 * </br>
 * @author ben</br>
 */
public class Feature {
	/**
	 * @return name of the feature
	 */
	public String getName() {
		return this.getClass().getSimpleName();
	}
	/**
	 * This should (if necessary):
	 * - configure the feature (FeatureHelper.getConfiguration)
	 * - give information about the loaded configuration (FeatureHelper.printConfiguration)
	 * - declare all dependencies (FeatureHelper.depends)
	 * - register all services provided by this feature (FeatureHelper.registerService)
	 */
	public void setup(FeatureHelper helper) {}
	/**
	 * This should setup all inter-feature arrangements
	 * (Those will mainly work via services)
	 */
	public void setupFeatureArrangements(ServiceProvider provider) {}
	public void setupCompilerGenerator(CompilerGenerator cg) {}

}
