package lfocc.features.samplefeature;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.service.ServiceManager;
import lfocc.framework.util.XML;

public class SampleFeature implements Feature {
	
	public static final String FEATURE_CONFIGURATION_SCHEMA =
			"features/lfocc/features/samplefeature/schema.xsd";
	
	private String option1;
	private String option2;
	private String option3;
	private String option4;

	@Override
	public String getName() {
		return "SampleFeature";
	}

	@Override
	public void configure(File config) {
		Document doc = XML.load(config, new File(FEATURE_CONFIGURATION_SCHEMA));
		option1 = doc.getElementsByTagName("option1").item(0).getTextContent();
		option2 = doc.getElementsByTagName("option2").item(0).getTextContent();
		option3 = doc.getElementsByTagName("option3").item(0).getTextContent();
		option4 = doc.getElementsByTagName("option4").item(0).getTextContent();
	}

	@Override
	public void setup() {}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {}

	@Override
	public List<String> getConfiguration() {
		List<String> cfgs = new ArrayList<String>();
		cfgs.add(String.format("option1 = '%s'", option1));
		cfgs.add(String.format("option2 = '%s'", option2));
		cfgs.add(String.format("option3 = '%s'", option3));
		cfgs.add(String.format("option4 = '%s'", option4));
		return cfgs;
	}

	@Override
	public Set<String> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void registerServices(ServiceManager serviceManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupFeatureArrangements(ServiceManager serviceManager) {
		// TODO Auto-generated method stub
		
	}
}
