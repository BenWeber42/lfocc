package lfocc.features.functions.ast;

import java.util.HashMap;
import java.util.Map;

public class MethodCollection {

	private Map<String, FunctionDeclaration> methods = new HashMap<String, FunctionDeclaration>();
	
	public void addMethod(FunctionDeclaration method) {
		methods.put(method.getName(), method);
	}
	
	public FunctionDeclaration getMethod(String name) {
		return methods.get(name);
	}
}
