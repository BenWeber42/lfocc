package lfocc.features.x86;

import java.io.File;
import java.util.Arrays;

import org.w3c.dom.Document;

import lfocc.features.variables.services.VariablesConfig;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FrameworkInterface;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.util.XML;

public class X86 extends Feature {
	
	public static final String X86_CONFIGURATION_SCHEMA =
			"features/lfocc/features/x86/ConfigSchema.xsd";

	private boolean javaEntry = false;
	private boolean cEntry = true;
	private VariablesConfig variables;
		
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

	public void setupFeatureArrangements(ServiceProvider provider) {
		if (provider.hasFeature("Variables"))
			variables = (VariablesConfig) provider.getService("Variables", "VariablesConfig");
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
		cg.addSource("lfocc.features.x86.backend",
				new File("features/lfocc/features/x86/backend/RegisterManager.java"));
		cg.addSource("lfocc.features.x86.backend", "CodeGenerator", generateCodeGenerator(cg));
		
		if (javaEntry)
			cg.addSource("lfocc.features.x86.backend",
					new File("features/lfocc/features/x86/backend/preparation/JavaEntryAdder.java"));
		else
			cg.addSource("lfocc.features.x86.backend.preparation",
					new File("features/lfocc/features/x86/backend/preparation/CEntryAdder.java"));

		cg.addSource("lfocc.features.x86.backend.generators",
				new File("features/lfocc/features/x86/backend/generators/FunctionCodeGenerator.java"));
		
		if (cg.hasFeature("Classes")) {
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/ClassCodeGenerator.java"));
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/MethodCodeGenerator.java"));
		}

		cg.addSource("lfocc.features.x86.backend.preparation",
				new File("features/lfocc/features/x86/backend/preparation/FunctionOffsetGenerator.java"));

		cg.addSource("lfocc.features.x86.backend.generators",
				new File("features/lfocc/features/x86/backend/generators/ExpressionCodeGenerator.java"));

		if (cg.hasFeature("Classes"))
			cg.addSource("lfocc.features.x86.backend.preparation",
					new File("features/lfocc/features/x86/backend/preparation/ClassPreparer.java"));

		if (cg.hasFeature("Variables") && variables.hasClassMembers())
			cg.addSource("lfocc.features.x86.backend.preparation",
					new File("features/lfocc/features/x86/backend/preparation/ClassVariablePreparer.java"));

		if (cg.hasFeature("Variables"))
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/VariableCodeGenerator.java"));

		if (cg.hasFeature("Assignments"))
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/AssignmentCodeGenerator.java"));

		if (cg.hasFeature("ControlFlow"))
			cg.addSource("lfocc.features.x86.backend.generators",
					new File("features/lfocc/features/x86/backend/generators/ControlFlowCodeGenerator.java"));
	}
	
	public String generateCodeGenerator(FrameworkInterface language) {
		String src = "";
		
		// LATER: organize imports
		src += "package lfocc.features.x86.backend;\n";
		src += "\n";
		src += "import java.util.List;\n";
		src += "\n";
		src += "import lfocc.framework.compiler.ast.ASTNode;\n";
		src += "import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;\n";
		src += "import lfocc.features.base.ast.ScopeKind;\n";
		src += "import lfocc.features.expressions.ast.Expression;\n";
		src += "import lfocc.framework.compiler.Backend.BackendFailure;\n";
		src += "import lfocc.features.x86.backend.RegisterManager;\n";
		src += "import lfocc.features.globalscope.ast.GlobalScope;\n";
		if (javaEntry)
			src += "import lfocc.features.x86.backend.preparation.JavaEntryAdder;\n";
		else
			src += "import lfocc.features.x86.backend.preparation.CEntryAdder;\n";
		src += "import lfocc.features.x86.backend.preparation.FunctionOffsetGenerator;\n";
		src += "import lfocc.features.x86.backend.generators.FunctionCodeGenerator;\n";
		src += "import lfocc.features.functions.ast.FunctionDeclaration;\n";
		src += "import lfocc.features.functions.ast.FunctionCall;\n";
		src += "import lfocc.features.functions.ast.ReturnStatement;\n";
		src += "import lfocc.features.x86.backend.generators.ExpressionCodeGenerator;\n";
		src += "import lfocc.features.expressions.ast.IntConst;\n";
		src += "import lfocc.features.expressions.ast.BooleanConst;\n";
		src += "import lfocc.features.expressions.ast.FloatConst;\n";
		src += "import lfocc.features.expressions.ast.BinaryOperatorExpression;\n";
		src += "import lfocc.features.expressions.ast.UnaryOperatorExpression;\n";
		if (language.hasFeature("Classes")) {
			src += "import lfocc.features.x86.backend.preparation.ClassPreparer;\n";
			src += "import lfocc.features.x86.backend.generators.ClassCodeGenerator;\n";
			src += "import lfocc.features.x86.backend.generators.MethodCodeGenerator;\n";
			src += "import lfocc.features.classes.ast.ClassDeclaration;\n";
			src += "import lfocc.features.classes.ast.NewOperator;\n";
			src += "import lfocc.features.classes.ast.NullExpression;\n";
			src += "import lfocc.features.classes.ast.ThisReference;\n";
			src += "import lfocc.features.classes.ast.CastExpression;\n";
		}
		if (language.hasFeature("Variables") && variables.hasClassMembers())
			src += "import lfocc.features.x86.backend.preparation.ClassVariablePreparer;\n";
		if (language.hasFeature("Variables")) {
			src += "import lfocc.features.x86.backend.generators.VariableCodeGenerator;\n";
			src += "import lfocc.features.variables.ast.VariableDeclaration;\n";
			src += "import lfocc.features.variables.ast.Variable;\n";
			src += "import lfocc.features.variables.ast.Attribute;\n";
		}
		if (language.hasFeature("Assignments")) {
			src += "import lfocc.features.x86.backend.generators.AssignmentCodeGenerator;\n";
			src += "import lfocc.features.assignments.ast.Assignment;\n";
		}
		if (language.hasFeature("ControlFlow")) {
			src += "import lfocc.features.x86.backend.generators.ControlFlowCodeGenerator;\n";
			src += "import lfocc.features.controlflow.ast.ConditionalSequence;\n";
			src += "import lfocc.features.controlflow.ast.WhileLoop;\n";
			src += "import lfocc.features.controlflow.ast.ForLoop;\n";
			src += "import lfocc.features.controlflow.ast.DoWhileLoop;\n";
		}
		src += "\n";
		src += "\n";
		src += "public class CodeGenerator implements CodeGeneratorInterface {\n";
		src += "   \n";
		src += "   private RegisterManager regs = new RegisterManager();\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// public RegisterManager getRegisterManager()
		////////////////////////////////////////////////////////////////////////
		src += "   @Override\n";
		src += "   public RegisterManager getRegisterManager() {\n";
		src += "      return regs;\n";
		src += "   }\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// public String getRuntime()
		////////////////////////////////////////////////////////////////////////
		src += "   private static String getRuntime() {\n";
		src += "      String src = \"\";\n";
		src += "      src += FunctionCodeGenerator.getRuntime();\n";
		src += "      return src;\n";
		src += "   }\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// public String generate(GlobalScope root)
		////////////////////////////////////////////////////////////////////////
		src += "   @Override\n";
		src += "   public String generate(GlobalScope root) throws BackendFailure {\n";
		src += "      \n";
		src += "      prepare(root);\n";
		src += "      \n";
		src += "      return getRuntime() + dispatch(root);\n";
		src += "   }\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// private void prepare(GlobalScope root)
		////////////////////////////////////////////////////////////////////////
		src += "   private void prepare(GlobalScope root) throws BackendFailure {\n";
		src += "      \n";
		if (javaEntry)
			src += "      JavaEntryAdder.addJavaEntry(root);\n";
		else
			src += "      CEntryAdder.addCEntry(root);\n";
		src += "      try {\n";
        src += "         new FunctionOffsetGenerator().visit(root);\n";
        if (language.hasFeature("Classes"))
        	src += "         new ClassPreparer().visit(root);\n";
        if (language.hasFeature("Variables") && variables.hasClassMembers())
        	src += "         new ClassVariablePreparer().visit(root);\n";
		src += "      } catch (VisitorFailure v) {\n";
		src += "         throw new BackendFailure(v.getMessage());\n";
		src += "      }\n";
		src += "      \n";
		src += "   }\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// public void String dispatch(List<ASTNode> nodes)
		////////////////////////////////////////////////////////////////////////
		src += "   @Override\n";
		src += "   public String dispatch(List<? extends ASTNode> nodes) throws BackendFailure {\n";
		src += "      \n";
		src += "      String src = \"\";\n";
		src += "      for (ASTNode node: nodes)\n";
		src += "         src += dispatch(node);\n";
		src += "      \n";
		src += "      return src;\n";
		src += "   }\n";
		src += "   \n";
		////////////////////////////////////////////////////////////////////////
		// public void String dispatch(ASTNode node)
		////////////////////////////////////////////////////////////////////////
		src += "   @Override\n";
		src += "   public String dispatch(ASTNode node) throws BackendFailure {\n";
		src += "      \n";
		src += "      if (node instanceof GlobalScope) {\n";
		src += "         \n";
		src += "         return dispatch(node.getChildren());\n";
		src += "         \n";
		src += "      } else if (node instanceof FunctionDeclaration) {\n";
		src += "         \n";
		src += "         return FunctionCodeGenerator.functionDeclaration((FunctionDeclaration) node, this);\n";
		src += "         \n";
		src += "      } else if (node instanceof ReturnStatement) {\n";
		src += "         \n";
		src += "         return FunctionCodeGenerator.returnStatement((ReturnStatement) node, this);\n";
		src += "         \n";
		src += "      } else if (node instanceof FunctionCall) {\n";
		src += "         \n";
		src += "         FunctionCall call = (FunctionCall) node;\n";
		src += "         \n";
		src += "         if (call.getDeclaration().extension(ScopeKind.class) == null) {\n";
		src += "            System.err.format(\"Function '%s' has no ScopeKind extension!\\n\", call.getName());\n";
		src += "            \n";
		src += "            \n";
		src += "         }\n";
		src += "         \n";
		src += "         assert call.getDeclaration().extension(ScopeKind.class) != null;\n";
		src += "         \n";
		if (language.hasFeature("Classes")) {
			src += "         if (call.getDeclaration().extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER) {\n";
			src += "            return MethodCodeGenerator.functionCall((FunctionCall) node, this);\n";
			src += "         } else {\n";
			src += "            return FunctionCodeGenerator.functionCall((FunctionCall) node, this);\n";
			src += "         }\n";
		} else {
			src += "         return FunctionCodeGenerator.functionCall((FunctionCall) node, this);\n";
		}
		src += "         \n";
		src += "      } else if (node instanceof IntConst) {\n";
		src += "         \n";
		src += "         return ExpressionCodeGenerator.intConst((IntConst) node, regs);\n";
		src += "         \n";
		src += "      } else if (node instanceof BooleanConst) {\n";
		src += "         \n";
		src += "         return ExpressionCodeGenerator.booleanConst((BooleanConst) node, regs);\n";
		src += "         \n";
		src += "      } else if (node instanceof FloatConst) {\n";
		src += "         \n";
		src += "         return ExpressionCodeGenerator.floatConst((FloatConst) node, regs);\n";
		src += "         \n";
		src += "      } else if (node instanceof BinaryOperatorExpression) {\n";
		src += "         \n";
		src += "         return ExpressionCodeGenerator.binaryOperator((BinaryOperatorExpression) node, this);\n";
		src += "         \n";
		src += "      } else if (node instanceof UnaryOperatorExpression) {\n";
		src += "         \n";
		src += "         return ExpressionCodeGenerator.unaryOperator((UnaryOperatorExpression) node, this);\n";
		src += "         \n";
		if (language.hasFeature("Classes")) {
			src += "      } else if (node instanceof ClassDeclaration) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.classDeclaration((ClassDeclaration) node, this);\n";
			src += "         \n";
			src += "      } else if (node instanceof NewOperator) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.newOperator((NewOperator) node, this);\n";
			src += "         \n";
			src += "      } else if (node instanceof NullExpression) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.nullExpression((NullExpression) node, regs);\n";
			src += "         \n";
			src += "      } else if (node instanceof ThisReference) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.thisReference((ThisReference) node, regs);\n";
			src += "         \n";
			src += "      } else if (node instanceof CastExpression) {\n";
			src += "         \n";
			src += "         return ClassCodeGenerator.castExpression((CastExpression) node, this);\n";
			src += "         \n";
		}
		if (language.hasFeature("Variables")) {
			src += "      } else if (node instanceof VariableDeclaration) {\n";
			src += "         \n";
			src += "         return VariableCodeGenerator.variableDeclaration((VariableDeclaration) node);\n";
			src += "         \n";
			src += "      } else if (node instanceof Variable) {\n";
			src += "         \n";
			src += "         return VariableCodeGenerator.variable((Variable) node, regs);\n";
			src += "         \n";
			src += "      } else if (node instanceof Attribute) {\n";
			src += "         \n";
			src += "         return VariableCodeGenerator.attribute((Attribute) node, this);\n";
			src += "         \n";
		}
		if (language.hasFeature("Assignments")) {
			src += "      } else if (node instanceof Assignment) {\n";
			src += "         \n";
			src += "         return AssignmentCodeGenerator.assignment((Assignment) node, this);\n";
			src += "         \n";
		}
		if (language.hasFeature("ControlFlow")) {
			src += "      } else if (node instanceof ConditionalSequence) {\n";
			src += "         \n";
			src += "         return ControlFlowCodeGenerator.conditionalSequence((ConditionalSequence) node, this);\n";
			src += "         \n";
			src += "      } else if (node instanceof WhileLoop) {\n";
			src += "         \n";
			src += "         return ControlFlowCodeGenerator.whileLoop((WhileLoop) node, this);\n";
			src += "         \n";
			src += "      } else if (node instanceof ForLoop) {\n";
			src += "         \n";
			src += "         return ControlFlowCodeGenerator.forLoop((ForLoop) node, this);\n";
			src += "         \n";
			src += "      } else if (node instanceof DoWhileLoop) {\n";
			src += "         \n";
			src += "         return ControlFlowCodeGenerator.doWhileLoop((DoWhileLoop) node, this);\n";
			src += "         \n";
		}
		src += "      } else {\n";
		src += "         throw new BackendFailure(String.format(\"Internal Error: Unknown AST node '%s'!\",\n";
		src += "                                    node.getClass().getSimpleName()));\n";
		src += "      }\n";
		src += "      \n";
		src += "   }\n";
		////////////////////////////////////////////////////////////////////////
		// public String getAddress(Expression node)
		////////////////////////////////////////////////////////////////////////
		src += "   @Override\n";
		src += "   public String getAddress(Expression node) throws BackendFailure {\n";
		src += "      \n";
		if (language.hasFeature("Variables")) {
			src += "      if (node instanceof Variable) {\n";
			src += "         return VariableCodeGenerator.getAddressOfVariable((Variable) node, regs);";
			src += "      } else if (node instanceof Attribute) {\n";
			src += "         return VariableCodeGenerator.getAddressOfAttribute((Attribute) node, this);";
			src += "      } else {\n";
			src += "         throw new BackendFailure(String.format(\"Internal Error: Can't take address of AST node '%s'!\",\n";
			src += "                                    node.getClass().getSimpleName()));\n";
			src += "      }\n";
		} else {
			src += "      throw new BackendFailure(" + 
						  "\"Internal Error: Can't take address of any AST node of language '" +
						  language.getLanguageName() + "'!\");\n";
		}
		src += "   }\n";
		src += "   \n";
		src += "}\n";

		return src;
	}
	
}
