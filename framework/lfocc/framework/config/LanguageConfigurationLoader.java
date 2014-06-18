package lfocc.framework.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lfocc.framework.util.Logger;
import lfocc.framework.util.XML;

public class LanguageConfigurationLoader {
	
	public static final String LANGUAGE_CONFIGURATION_SCHEMA = 
			"configs/schema.xsd";
	
	private GlobalConfiguration globalConfig = null;
	private Map<String, File> featureConfigurations = null;
	
	public void process(File file) {
		
		Document doc = XML.load(file, new File(LANGUAGE_CONFIGURATION_SCHEMA));
		
		globalConfig = new GlobalConfiguration();
		globalConfig.name = doc.getDocumentElement().getElementsByTagName("name").item(0).getTextContent();

		featureConfigurations = new HashMap<String, File>();
		NodeList features = doc.getDocumentElement().getElementsByTagName("feature");
		for (int i = 0; i < features.getLength(); ++i) {
			Node f = features.item(i);
			String name = f.getAttributes().getNamedItem("name").getTextContent();
			if (featureConfigurations.containsKey(name)) {
				Logger.error(String.format("Feature '%s' mentioned twice in LanguageConfiguration file!", name));
			} else {
				Node config = f.getAttributes().getNamedItem("config");
				featureConfigurations.put(name,  new File(file.getParent() + "/" + (config != null ? config.getTextContent() : null)));
			}
		}
		
	}
	
	public GlobalConfiguration getGlobalConfiguration() {
		return globalConfig;
	}
	
	public Set<String> getFeatures() {
		return new HashSet<String>(featureConfigurations.keySet());
	}
	
	public File getConfigurationForFeature(String feature) {
		return featureConfigurations.get(feature);
	}

}
