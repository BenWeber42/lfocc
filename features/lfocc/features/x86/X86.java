package lfocc.features.x86;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.util.XML;

public class X86 extends Feature {
	
	public static final String X86_CONFIGURATION_SCHEMA =
			"features/lfocc/features/x86/ConfigSchema.xsd";

	private boolean javaEntry = false;
	private boolean cEntry = true;
		
	/*
	 * A Java-style entry point looks like this:
	 * 
	 *    class Main { // the Main class may extend other classes too
	 *       void main() {
	 *       	// this functions will be set as entry point
	 *       }
	 *    }
	 *    
	 * A C-style entry point looks like this:
	 * 
	 *    void main() {
	 *    	// this functions will be set as entry point
	 *    }
	 */
	
	@Override
	public void setup(FeatureHelper helper) {
		
		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(X86_CONFIGURATION_SCHEMA));
			
			javaEntry = XML.getStringOption(cfg, "EntryPoint").equals("JavaStyle");
			cEntry = XML.getStringOption(cfg, "EntryPoint").equals("CStyle");
		
		}

		assert javaEntry != cEntry;
		
		helper.printConfiguration(Arrays.asList(
				"Entry-point = " + (javaEntry ? "Java-style" : "C-style")));

		helper.depends("Functions"); // for the entry point
		
		if (javaEntry)
			helper.depends("Classes"); // for the Main class
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		cg.addSource("lfocc.features.x86.semantics", 
				new File("features/lfocc/features/x86/semantics/EntryPointFailure.java"));
	
		if (javaEntry) {
			cg.addSource("lfocc.features.x86.semantics", 
					new File("features/lfocc/features/x86/semantics/JavaEntryChecker.java"));
			cg.getSemanticsGenerator().addTransformer(9000,
					"lfocc.features.x86.semantics", "JavaEntryChecker");
		}
		
		if (cEntry) {
			cg.addSource("lfocc.features.x86.semantics", 
					new File("features/lfocc/features/x86/semantics/CEntryChecker.java"));
			cg.getSemanticsGenerator().addTransformer(9000,
					"lfocc.features.x86.semantics", "CEntryChecker");
		}
		
		cg.addSource("lfocc.features.x86.backend",
				new File("features/lfocc/features/x86/backend/X86Backend.java"));
		cg.getBackendGenerator().setBackend(
				"lfocc.features.x86.backend", "X86Backend");

	}
	
}