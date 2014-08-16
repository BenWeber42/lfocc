package lfocc.features.functions.ast;

import java.util.HashMap;
import java.util.Map;

public class MethodCollection {

	private Map<String, FunctionSymbol> methods = new HashMap<String, FunctionSymbol>();
	
	public void addMethod(FunctionSymbol method) {
		methods.put(method.getNode().getName(), method);
	}
	
	public FunctionSymbol getMethod(String name) {
		return methods.get(name);
	}
}
