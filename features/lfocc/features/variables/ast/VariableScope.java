package lfocc.features.variables.ast;

import java.util.HashMap;
import java.util.Map;

public class VariableScope {

	/*
	 * This should probably be in a Scope feature as a Generic class
	 * (Generic in the second argument for the variables member)
	 */

	private VariableScope parent;
	private Map<String, VariableDeclaration> variables =
			new HashMap<String, VariableDeclaration>();
	
	public VariableScope(VariableScope parent) {
		this.parent = parent;
	}
	
	public void addVariable(VariableDeclaration var) {
		variables.put(var.getName(), var);
	}
	
	public VariableDeclaration getVariable(String name) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		} else if (parent != null) {
			return parent.getVariable(name);
		} else {
			return null;
		}
	}
	
	public VariableScope getParent() {
		return parent;
	}
}
