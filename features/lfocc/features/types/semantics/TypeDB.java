package lfocc.features.types.semantics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import lfocc.features.types.ast.TypeSymbol;


public enum TypeDB {
	INSTANCE;
	
	private Map<String, TypeSymbol> types = new HashMap<String, TypeSymbol>();

	public void addType(TypeSymbol type) {
		assert !types.containsKey(type.getName());
		types.put(type.getName(), type);
	}
	
	public TypeSymbol getType(String name) {
		return types.get(name);
	}
	
	public Iterator<TypeSymbol> iterator() {
		return types.values().iterator();
	}

}
