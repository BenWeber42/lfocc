package lfocc.features.javali;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.features.variables.services.VariablesConfig;
import lfocc.features.functions.services.FunctionsConfig;
import lfocc.framework.util.XML;

/**
 * The Javali back-end
 */
public class Javali extends Feature {
	
	public static final String JAVALI_CONFIGURATION_SCHEMA =
			"features/lfocc/features/javali/ConfigSchema.xsd";
	private static final String name = "JavaliBackend";
	private String _package;
	
	private boolean globals = false;
	private boolean anyTransformer = false;
	
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
	
	@Override
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
		
		helper.depends("Functions"); // for the entry point
		helper.depends("Variables"); // for class attributes
			
		// this feature isn't dependent on the 'classes' feature in the sense
		// that the base feature adds all necessary classes of the 'classes'
		// feature anyways.
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		globals =
				((VariablesConfig) services.getService("Variables", "VariablesConfig")).hasGlobals() ||
				((FunctionsConfig) services.getService("Functions", "FunctionsConfig")).hasGlobals();
		
		anyTransformer = globals;
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		_package = "lfocc.compilers." + cg.getLanguageName() + ".backend";
		cg.addSource(_package, name, generateBackend(cg));
		cg.getBackendGenerator().setBackend(_package, name);
		
		cg.addSource("lfocc.features.javali.semantics", 
				new File("features/lfocc/features/javali/semantics/EntryPointFailure.java"));
		
		if (!cg.hasFeature("Assignments")) {
			// this is required to add assignments for the globals attribute
			cg.addSource("lfocc.features.assignments.ast",
					new File("features/lfocc/features/assignments/ast/Assignment.java"));
		}
		
		if (javaEntry) {
			cg.addSource("lfocc.features.javali.semantics", 
					new File("features/lfocc/features/javali/semantics/JavaEntryChecker.java"));
			cg.getSemanticsGenerator().addTransformer(9000,
					"lfocc.features.javali.semantics", "JavaEntryChecker");
		}
		
		if (cEntry) {
			cg.addSource("lfocc.features.javali.semantics", 
					new File("features/lfocc/features/javali/semantics/CEntryChecker.java"));
			cg.getSemanticsGenerator().addTransformer(9000,
					"lfocc.features.javali.semantics", "CEntryChecker");
		}
		
		if (globals) {
			cg.addSource("lfocc.features.javali.transformers", 
					new File("features/lfocc/features/javali/transformers/GlobalScopeAdder.java"));
		}
	}
	
	private String generateBackend(FrameworkInterface framework) {
		String src = "";
		
		/*
		 * Before code can be generated the following is to be done:
		 * 
		 * - To support globals (variables & functions) add GlobalScope class
		 *   and add globals attribute of type GlobalScope to all classes.
		 *   (This will also require to escape class and attribute names)
		 *   Then replace global variable usage to use the globals attribute.
		 *   Also set the globals attribute whenever an instance of a class is
		 *   created (new operator).
		 *   
		 * - To support different variable scopes in functions assign unique
		 *   variable names to existing function local variables and move
		 *   variable declarations to the top of a function (the only place
		 *   where Javali supports function local variable declarations).
		 * 
		 * - To support different control-flow constructs (for, do-while etc)
		 *   rewrite them to control-flow constructs that Javali supports.
		 * 
		 * - To support different entry-point styles (C-style & Java-style)
		 *   rewrite entry-point to a Javali-style entry-point (this may require
		 *   renaming classes).
		 *   
		 * ... potentially more
		 * 
		 * Afterwards code generation should be straight forward.
		 * 
		 */
		
		src += "package " + _package + ";\n";
		src += "\n";
		src += "import java.io.File;\n";
		src += "\n";
		src += "import lfocc.framework.compiler.Backend;\n";
		src += "import lfocc.framework.compiler.ast.ASTNode;\n";
		src += "import lfocc.framework.compiler.ast.ASTVisitor;\n";
		src += "import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;\n";
		if (anyTransformer) {
			src += "import lfocc.features.javali.transformers.*;\n";
		}
		src += "\n";
		src += "public class " + name + " implements Backend {\n";
		src += "\n";
		src += "   @Override\n";
		src += "   public void generate(File out, ASTNode root) throws BackendFailure {\n";
		src += "      transform(root);\n";
		src += "      // TODO\n";
		src += "   }\n";
		src += "\n";
		src += "   /**\n";
		src += "    * Transforms all unsupported AST nodes into supported AST nodes\n";
		src += "    */\n";
		src += "   public void transform(ASTNode root) throws BackendFailure {\n";
		src += "      \n";
		if (anyTransformer) {
			src += "      try {\n";
		}
		if (globals) {
			src += "         ASTVisitor globalScopeAdder = new GlobalScopeAdder();\n";
			src += "         globalScopeAdder.visit(root);\n";
		}
		if (anyTransformer) {
			src += "      } catch (VisitorFailure e) {\n";
			src += "         throw new BackendFailure(e.getMessage());\n";
			src += "      }\n";
		}
		src += "      \n";
		src += "   }\n";
		src += "\n";
		src += "}\n";
		
		return src;
	}

}
