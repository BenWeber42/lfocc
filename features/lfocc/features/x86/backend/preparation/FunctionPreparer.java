package lfocc.features.x86.backend.preparation;

import java.util.HashMap;
import java.util.Map;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/**
 * Generates the offsets of the locals for each function
 * and sets the function declaration for each return statement
 */
public class FunctionPreparer extends ASTVisitor {
	
	private FunctionDeclaration decl = null;

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration)
			functionDeclaration((FunctionDeclaration) node);
		else if (node instanceof ReturnStatement)
			returnStatement((ReturnStatement) node);
		else
			super.visit(node);
	}
	
	private void returnStatement(ReturnStatement ret) {
		assert decl != null;
		ReturnDeclaration.setDeclaration(ret, decl);
	}
	
	private void functionDeclaration(FunctionDeclaration funcDecl) throws VisitorFailure {
		funcDecl.extend(new FunctionOffsets(funcDecl));
		
		decl = funcDecl;
		super.visit(funcDecl);
		decl = null;
	}
	
	public static class ReturnDeclaration {
		private final FunctionDeclaration functionDeclaration;
		
		private ReturnDeclaration(FunctionDeclaration decl) {
			this.functionDeclaration = decl;
		}
		
		public static void setDeclaration(ReturnStatement ret, FunctionDeclaration decl) {
			assert decl != null;
			ret.extend(new ReturnDeclaration(decl));
		}
		
		public static FunctionDeclaration getDeclaration(ReturnStatement ret) {
			ReturnDeclaration decl = ret.extension(ReturnDeclaration.class);
			assert decl != null;
			return decl.functionDeclaration;
		}
	}
	
	public static class LocalVariableOffset {
		public final int offset;
		
		private LocalVariableOffset(int offset) {
			this.offset = offset;
		}
		
		public static void setOffset(VariableDeclaration var, int offset) {
			assert var.extension(ScopeKind.class) == ScopeKind.LOCAL;
			var.extend(new LocalVariableOffset(offset));
		}
		
		public static int getOffset(VariableDeclaration var) {
			assert var.extension(ScopeKind.class) == ScopeKind.LOCAL;
			LocalVariableOffset offset = var.extension(LocalVariableOffset.class);
			assert offset != null;
			return offset.offset;
		}
	}
	
	public static class FunctionOffsets {
		
		/*
		 * Uses cdecl calling convention
		 */

		/** size of locals in bytes */
		private int localSize = 0;
		private int offset;
		private Map<String, Integer> offsets = new HashMap<String, Integer>();
		
		public FunctionOffsets(FunctionDeclaration funcDecl) {
			
			offset = funcDecl.getParameters().size()*CodeGeneratorHelper.WORD_SIZE;

			int reverser = 0;
			for (VariableDeclaration varDecl: funcDecl.getParameters()) {
				offsets.put(varDecl.getName(), offset - reverser);
				LocalVariableOffset.setOffset(varDecl, offset - reverser);
				reverser += CodeGeneratorHelper.WORD_SIZE;
			}
			
			// GCC pushes the this pointer last
			if (funcDecl.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER) {
				offset += CodeGeneratorHelper.WORD_SIZE;
				offsets.put("this", offset);
			}

			// at this offset there will be the return address on the stack
			offset += CodeGeneratorHelper.WORD_SIZE;
			localSize = offset;
			
			for (VariableDeclaration varDecl: funcDecl.extension(VariableScope.class).getLocalIterable()) {
				
				if (offsets.containsKey(varDecl.getName()))
					continue; // must be a parameter
				
				offset += CodeGeneratorHelper.WORD_SIZE;
				offsets.put(varDecl.getName(), offset);
				LocalVariableOffset.setOffset(varDecl, offset);
			}
			
			localSize = offset - localSize;
		}
		
		/**
		 * Relative to the first local variable (non-parameter)
		 */
		public int offset(String name) {
			assert offsets.containsKey(name);
			return offsets.get(name);
		}
		
		/**
		 * Returns the size required for all locals (non-parameters)
		 */
		public int getSize() {
			return localSize;
		}
	}
}
