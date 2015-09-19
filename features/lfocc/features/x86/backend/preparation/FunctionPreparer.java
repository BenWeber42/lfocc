package lfocc.features.x86.backend.preparation;

import java.util.HashSet;
import java.util.Set;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.features.x86.backend.CodeGeneratorHelper;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/**
 * Generates the offsets of the locals for each function (also in nested local scopes).
 * Sets the function declaration for each return statement.
 */
public class FunctionPreparer extends ASTVisitor {
	
	private FunctionDeclaration decl = null;
	private int offset = -1;

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration) {
			functionDeclaration((FunctionDeclaration) node);
		} else if (node instanceof ReturnStatement) {
			returnStatement((ReturnStatement) node);
		} else if (decl != null && node.extension(VariableScope.class) != null) {
			localScope(node);
		} else {
			super.visit(node);
		}
	}
	
	private void localScope(ASTNode node) throws VisitorFailure {
			VariableScope scope = node.extension(VariableScope.class);
			assert scope != null && decl != null && offset != -1;

			int oldOffset = offset;
			
			for (VariableDeclaration var: scope.getLocalIterable()) {
				offset -= CodeGeneratorHelper.WORD_SIZE;
				LocalVariableOffset.setOffset(var, offset);
			}
			int size = oldOffset - offset;
			ScopeSize.setScopeSize(node, size);
			super.visit(node);
			offset = oldOffset;
	}
	
	private void returnStatement(ReturnStatement ret) {
		assert decl != null;
		ReturnDeclaration.setDeclaration(ret, decl);
	}
	
	private void functionDeclaration(FunctionDeclaration funcDecl) throws VisitorFailure {
		FunctionOffsets offsets = new FunctionOffsets(funcDecl);
		funcDecl.extend(offsets);
		
		decl = funcDecl;
		// 3*WORD_SIZE to push callee-saved registers ebx, edi, esi
		offset = offsets.getSize() - 3*CodeGeneratorHelper.WORD_SIZE;
		super.visit(funcDecl);
		offset = -1;
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
	
	/**
	 * Determines the offset in bytes relative to %ebp for local variables
	 * (function parameters & function locals)
	 */
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
	
	public static class ScopeSize {
		private final int size;
		
		private ScopeSize(int size) {
			this.size = size;
		}
		
		public static void setScopeSize(ASTNode node, int size) {
			assert node.extension(VariableScope.class) != null;
			node.extend(new ScopeSize(size));
		}
		
		public static String enterScope(ASTNode node) {
			ScopeSize size = node.extension(ScopeSize.class);
			
			if (size == null)
				return "";
			
			return "   subl $" + size.size + ", %esp\n";
		}
		
		public static String leaveScope(ASTNode node) {
			ScopeSize size = node.extension(ScopeSize.class);
			
			if (size == null)
				return "";
			
			return "   addl $" + size.size + ", %esp\n";
			
		}
	}
	
	public static class FunctionOffsets {
		
		/*
		 * Uses cdecl calling convention
		 */

		/** size of locals in bytes */
		private int localSize = 0;

		public FunctionOffsets(FunctionDeclaration funcDecl) {
			
			Set<String> offsets = new HashSet<String>();

			// skip %ebp on stack and return address on stack
			int parameterOffset = CodeGeneratorHelper.WORD_SIZE;

			// GCC pushes the this pointer last
			if (funcDecl.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER) {
				parameterOffset += CodeGeneratorHelper.WORD_SIZE;
				offsets.add("this");
			}

			for (VariableDeclaration varDecl: funcDecl.getParameters()) {
				parameterOffset += CodeGeneratorHelper.WORD_SIZE;
				LocalVariableOffset.setOffset(varDecl, parameterOffset);
				offsets.add(varDecl.getName());
			}

			int localOffset = 0;
			
			for (VariableDeclaration varDecl:funcDecl.extension(VariableScope.class).getLocalIterable()) {
				
				if (offsets.contains(varDecl.getName()))
					continue; // must be a parameter
				
				localOffset -= CodeGeneratorHelper.WORD_SIZE;
				LocalVariableOffset.setOffset(varDecl, localOffset);
				offsets.add(varDecl.getName());
			}
			
			localSize = -localOffset;
		}
		
		/**
		 * Returns the size required for all locals (non-parameters)
		 */
		public int getSize() {
			return localSize;
		}
	}
}
