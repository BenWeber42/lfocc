package lfocc.features.x86.backend.preparation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.classes.ast.ClassType;
import lfocc.features.classes.ast.NewOperator;
import lfocc.features.expressions.ast.Expression;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.FunctionScope;
import lfocc.features.functions.ast.MethodCall;
import lfocc.features.functions.ast.VoidType;
import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.features.types.ast.TypeSymbol;
import lfocc.features.types.semantics.TypeDB;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.features.x86.backend.CodeGeneratorHelper.EntryPoint;
import lfocc.features.x86.backend.CodeGeneratorHelper.NoNameEscape;
import lfocc.framework.compiler.ast.ASTNode;


public class JavaEntryAdder {
	
	private static final ASTNode entryPoint;
	private static final MethodCall mainCall;
	private static final NewOperator newMain;
	
	static {
		entryPoint = new FunctionDeclaration(
				new VoidType(), // return type
				"main", // name
				new ArrayList<VariableDeclaration>(), // parameters
				// code
				Arrays.<ASTNode>asList(
						// new Main.main();
						mainCall = new MethodCall("main", // name
								newMain = new NewOperator("Main"), // expression
								(List<Expression>) new ArrayList<Expression>() // parameters
								)
						)
				);

		entryPoint.extend(ScopeKind.GLOBAL);
		entryPoint.extend(new NoNameEscape());
		entryPoint.extend(new EntryPoint());
	}

	public static void addJavaEntry(GlobalScope globalScope) {
		TypeSymbol mainType = TypeDB.INSTANCE.getType("Main");
		assert mainType != null && mainType instanceof ClassType;
		
		ClassType mainClass = (ClassType) mainType;
		
		newMain.setType(mainClass);
		mainCall.setDeclaration(mainClass.getNode().extension(FunctionScope.class).getMethod("main"));
		mainCall.setExpression(false);
		entryPoint.extend(new VariableScope(globalScope.extension(VariableScope.class)));
		
		globalScope.add(entryPoint);
	}

}
