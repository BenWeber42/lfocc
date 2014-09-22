package lfocc.features.functions.semantics;

import java.util.Iterator;
import java.util.List;

import lfocc.features.controlflow.ast.Conditional;
import lfocc.features.controlflow.ast.ElseConditional;
import lfocc.features.controlflow.ast.IfConditional;
import lfocc.features.functions.ast.FunctionDeclaration;
import lfocc.features.functions.ast.ReturnStatement;
import lfocc.features.functions.ast.VoidType;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.compiler.ast.ASTVisitor;

/*
 * Checks for (independent of Types feature but dependent on ControlFlow feature):
 * - Correct use of return statements
 *    - Function returns correctly in all cases (given that it terminates) 
 */
public class FunctionReturnChecker extends ASTVisitor {

	private boolean returns = false;
	
	@Override
	public void visit(ASTNode node) throws VisitorFailure {
		if (node instanceof FunctionDeclaration) {
			functionDeclaration((FunctionDeclaration) node);
		} else if (node instanceof Conditional) {
			conditional((Conditional) node);
		} else if (node instanceof IfConditional) {
			ifConditional((IfConditional) node);
		} else if (node instanceof ElseConditional) {
			elseConditional((ElseConditional) node);
		} else if (node instanceof ReturnStatement) {
			returnStatement((ReturnStatement) node);
		} else {
			visit(node.getChildren());
		}
	}

	private void returnStatement(ReturnStatement ret) {
		returns = true;
	}

	private void conditional(Conditional cond) throws VisitorFailure {
		
		returns = false;
		
		if (cond.getElse() == null)
			return;
			
		Iterator<IfConditional> it = cond.getConditionals().iterator();
		while (it.hasNext()) {
			visit(it.next());
			if (!returns) {
				return;
			}
		}
		
		visit(cond.getElse());
	}

	private void elseConditional(ElseConditional cond) throws VisitorFailure {
		returns = returnsAny(cond.getCode());
	}

	private void ifConditional(IfConditional cond) throws VisitorFailure {
		returns = returnsAny(cond.getCode());
	}

	private void functionDeclaration(FunctionDeclaration func) throws VisitorFailure {
		if (!(func.getReturnType() instanceof VoidType) && !returnsAny(func.getChildren())) {
			throw new ReturnFailure(String.format(
					"Function '%s' doesn't return in all cases!",
					func.getName()));
		}
	}
	
	private boolean returnsAny(List<ASTNode> nodes) throws VisitorFailure {
		returns = false;
		Iterator<ASTNode> it = nodes.iterator();
		while (it.hasNext()) {
			visit(it.next());
			if (returns)
				return true;
		}
		
		return false;
	}
}
