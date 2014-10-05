package lfocc.features.javali;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.util.XML;

/**
 * The Javali back-end
 */
public class Javali extends Feature {
	
	public static final String JAVALI_CONFIGURATION_SCHEMA =
			"features/lfocc/features/javali/ConfigSchema.xsd";
	private static final String name = "JavaliBackend";
	private String _package;
	
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
	
	// whether a Java-style entry point is configured
	private boolean javaEntry = false;
	// whether a C-style entry point is configured
	private boolean cEntry = true;
	
	public void setup(FeatureHelper helper) {
		
		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(JAVALI_CONFIGURATION_SCHEMA));
			
			javaEntry = XML.getStringOption(cfg, "EntryPoint").equals("JavaStyle");
			cEntry = XML.getStringOption(cfg, "EntryPoint").equals("CStyle");
		
		}
		
		assert javaEntry != cEntry;
		
		helper.printConfiguration(Arrays.asList(
				"Entry-point = " + (javaEntry ? "Java-style" : "C-style")));
		
		helper.depends("Functions");
		if (javaEntry)
			helper.depends("Classes");
	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		_package = "lfocc.compilers." + cg.getLanguageName() + ".backend";
		cg.addSource(_package, name, generateBackend(cg));
		cg.getBackendGenerator().setBackend(_package, name);
		
		// TODO
		if (javaEntry) {
			cg.addSource("lfocc.features.javali.semantics", 
					new File("features/lfocc/features/javali/semantics/JavaEntryChecker.java"));
		}
	}
	
	private String generateBackend(FrameworkInterface framework) {
		String src = "";
		
		src += "package " + _package + ";\n";
		src += "\n";
		src += "import lfocc.framework.compiler.Backend;\n";
		src += "import lfocc.framework.compiler.ast.ASTNode;\n";
		src += "\n";
		src += "public class " + name + " implements Backend {\n";
		src += "\n";
		src += "   @Override\n";
		src += "   public void generate(StringBuilder out, ASTNode root) {\n";
		src += "      // TODO\n";
		src += "      out.append(\"Hello, World!\");\n";
		src += "   }\n";
		src += "\n";
		src += "}\n";
		
		return src;
	}

}
