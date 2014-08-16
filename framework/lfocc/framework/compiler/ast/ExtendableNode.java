package lfocc.framework.compiler.ast;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtendableNode implements ASTNode {

	private Map<Class<?>, Object> extensions = new HashMap<Class<?>, Object>();
	
	@Override
	public void attach(Class<?> type, Object extension) {
		assert !extensions.containsKey(type);
		
		extensions.put(type, extension);
	}
	
	@Override
	public Object extension(Class<?> type) {
		return extensions.get(type);
	}

}
