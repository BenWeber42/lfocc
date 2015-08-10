package lfocc.features.x86.backend.preparation;

import java.util.HashMap;
import java.util.Map;

import lfocc.features.base.ast.ScopeKind;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.variables.ast.VariableDeclaration;
import lfocc.features.variables.ast.VariableScope;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

public class FunctionOffsetGenerator extends ASTVisitor {
	
	private static final int WORD_SIZE = 4;

	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration)
			functionDeclaration((FunctionDeclaration) node);
		else
			super.visit(node);
	}
	
	private void functionDeclaration(FunctionDeclaration funcDecl) {
		funcDecl.extend(new FunctionOffsets(funcDecl));
	}
	
	public static class FunctionOffsets {
		private int localOffset = 0;
		private int parameterOffset = 3*WORD_SIZE;
		private Map<String, Integer> offsets = new HashMap<String, Integer>();
		
		public FunctionOffsets(FunctionDeclaration funcDecl) {
			
			for (VariableDeclaration varDecl: funcDecl.getParameters()) {
				offsets.put(varDecl.getName(), parameterOffset);
				parameterOffset += WORD_SIZE;
			}
			
			if (funcDecl.extension(ScopeKind.class) == ScopeKind.CLASS_MEMBER) {
				offsets.put("this", parameterOffset);
				parameterOffset += WORD_SIZE;
			}
			
			for (VariableDeclaration varDecl: funcDecl.extension(VariableScope.class).getLocalIterable()) {
				
				if (offsets.containsKey(varDecl.getName()))
					continue;
				
				offsets.put(varDecl.getName(), localOffset);
				localOffset -= WORD_SIZE;
			}
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
			return localOffset;
		}
	}
}
