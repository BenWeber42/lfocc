package lfocc.features.functions.ast;

import java.util.HashMap;
import java.util.Map;

public class FunctionScope {

	private FunctionScope parent;
	private Map<String, FunctionDeclaration> functions = new HashMap<String, FunctionDeclaration>();
	
	public FunctionScope(FunctionScope parent) {
		this.parent = parent;
	}
	
	public void addMethod(FunctionDeclaration method) {
		functions.put(method.getName(), method);
	}
	
	public FunctionDeclaration getMethod(String name) {
		if (functions.containsKey(name)) {
			return functions.get(name);
		} else if (parent != null) {
			return parent.getMethod(name);
		} else {
			return null;
		}
	}
	
	public FunctionDeclaration getLocalMethod(String name) {
		return functions.get(name);
	}
	
	public FunctionScope getParent() {
		return parent;
	}
}
