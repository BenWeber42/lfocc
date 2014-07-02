package lfocc.features.variables;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.service.ServiceManager;
import lfocc.framework.util.Logger;
import lfocc.framework.util.XML;

public class Variables implements Feature {
	
	public static final String VARIABLES_CONFIGURATION_SCHEMA =
			"./features/lfocc/features/variables/configSchema.xsd";
	
	private boolean funcParams = false;
	private boolean funcLocals = false;
	private boolean globals = false;
	private boolean classMembers = false;

	@Override
	public String getName() {
		return "Variables";
	}

	@Override
	public void configure(File config) {
		if (config == null)
			return;

		Document cfg = XML.load(config, new File(VARIABLES_CONFIGURATION_SCHEMA));
		funcParams = cfg.getElementsByTagName("FunctionParameters").item(0).getTextContent().equals("true");
		funcLocals = cfg.getElementsByTagName("FunctionLocals").item(0).getTextContent().equals("true");
		globals = cfg.getElementsByTagName("Globals").item(0).getTextContent().equals("true");
		classMembers = cfg.getElementsByTagName("ClassMembers").item(0).getTextContent().equals("true");
	}

	@Override
	public void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getConfiguration() {
		return Arrays.asList(
				"FunctionParameters = " + funcParams,
				"FunctionLocals = " + funcLocals,
				"Globals = " + globals,
				"ClassMembers = " + classMembers
				);
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
