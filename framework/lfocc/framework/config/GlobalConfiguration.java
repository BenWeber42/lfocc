package lfocc.framework.config;

/**
 * Class representing 
 * @author ben
 *
 */
public class GlobalConfiguration {

	// name of the language
	String name;
	// features used for this language
	String[] features;
	// whether debugging information should be printed
	boolean debug = false;
	
	public String name() {
		return name;
	}
	
	public String[] getFeatures() {
		return features;
	}
	
	public boolean getDebug() {
		return debug;
	}

}
