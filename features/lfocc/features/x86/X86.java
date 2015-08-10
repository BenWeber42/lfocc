package lfocc.features.x86;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
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
		helper.depends("GlobalScope"); // can't generate any code otherwise
		
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
		
		cg.addSource("lfocc.features.x86.backend",
				new File("features/lfocc/features/x86/backend/CodeGeneratorInterface.java"));
		cg.addSource("lfocc.features.x86.backend",
				new File("features/lfocc/features/x86/backend/CodeGeneratorHelper.java"));
		cg.addSource("lfocc.features.x86.backend", "CodeGenerator", generateCodeGenerator(cg));
		
		if (javaEntry)
			cg.addSource("lfocc.features.x86.backend",
					new File("features/lfocc/features/x86/backend/preparation/JavaEntryAdder.java"));
		else
			cg.addSource("lfocc.features.x86.backend.preparation",
					new File("features/lfocc/features/x86/backend/preparation/CEntryAdder.java"));

		cg.addSource("lfocc.features.x86.backend.generators",
				new File("features/lfocc/features/x86/backend/generators/FunctionCodeGen.java"));
		
		if (cg.hasFeature("Classes"))
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/ClassCodeGenerator.java"));

		cg.addSource("lfocc.features.x86.backend.preparation",
				new File("features/lfocc/features/x86/backend/preparation/FunctionOffsetGenerator.java"));
	}
	
	public String generateCodeGenerator(FrameworkInterface language) {
		String src = "";
		
		// TODO: finish
		
		// LATER: organize imports
		src += "package lfocc.features.x86.backend;\n";
		src += "\n";
		src += "import lfocc.framework.compiler.ast.ASTNode;\n";
		src += "import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;\n";
		src += "import lfocc.framework.compiler.Backend.BackendFailure;\n";
		src += "import lfocc.features.globalscope.ast.GlobalScope;\n";
		if (javaEntry)
			src += "import lfocc.features.x86.backend.preparation.JavaEntryAdder;\n";
		else
			src += "import lfocc.features.x86.backend.preparation.CEntryAdder;\n";
		src += "import lfocc.features.x86.backend.preparation.FunctionOffsetGenerator;\n";
		src += "import lfocc.features.x86.backend.generators.FunctionCodeGen;\n";
		src += "import lfocc.features.functions.ast.FunctionDeclaration;\n";
		if (language.hasFeature("Classes")) {
			src += "import lfocc.features.x86.backend.generators.ClassCodeGenerator;\n";
			src += "import lfocc.features.classes.ast.ClassDeclaration;\n";
		}
		src += "\n";
		src += "\n";
		src += "public class CodeGenerator implements CodeGeneratorInterface {\n";
		src += "   \n";
		src += "   \n";
		src += "   @Override\n";
		src += "   public String generate(GlobalScope root) throws BackendFailure {\n";
		src += "      \n";
		src += "      prepare(root);\n";
		src += "      \n";
		src += "      return dispatch(root);\n";
		src += "   }\n";
		src += "   \n";
		src += "   private void prepare(GlobalScope root) throws BackendFailure {\n";
		src += "      \n";
		if (javaEntry)
			src += "      JavaEntryAdder.addJavaEntry(root);\n";
		else
			src += "      CEntryAdder.addCEntry(root);\n";
		src += "      try {\n";
        src += "      new FunctionOffsetGenerator().visit(root);\n";
		src += "      } catch (VisitorFailure v) {\n";
		src += "         throw new BackendFailure(v.getMessage());\n";
		src += "      }\n";
		src += "      \n";
		src += "   }\n";
		src += "   \n";
		src += "   @Override\n";
		src += "   public String dispatch(ASTNode node) throws BackendFailure {\n";
		src += "      \n";
		src += "      if (node instanceof GlobalScope) {\n";
		src += "         String src = \"\";\n";
		src += "         \n";
		src += "         for (ASTNode child: node.getChildren())\n";
		src += "            src += dispatch(child);\n";
		src += "         \n";
		src += "         return src;\n";
		src += "      } else if (node instanceof FunctionDeclaration) {\n";
		src += "         \n";
		src += "         return FunctionCodeGen.functionDeclaration((FunctionDeclaration) node);\n";
		src += "         \n";
		if (language.hasFeature("Classes")) {
			src += "      } else if (node instanceof ClassDeclaration) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.classDeclaration((ClassDeclaration) node);\n";
			src += "         \n";
		}
		if (language.hasFeature("Variables")) {
			// TODO
		}
		src += "      } else {\n";
		src += "         throw new BackendFailure(String.format(\"Internal Error: Unknown AST node '%s'!\",\n";
		src += "                                    node.getClass().getSimpleName()));\n";
		src += "      }\n";
		src += "      \n";
		src += "   }\n";
		src += "   \n";
		src += "}\n";

		return src;
	}
	
}
