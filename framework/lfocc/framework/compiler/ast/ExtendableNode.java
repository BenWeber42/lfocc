package lfocc.framework.compiler.ast;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtendableNode implements ASTNode {

	private Map<Class<?>, Object> extensions = new HashMap<Class<?>, Object>();
	
	@Override
	public void extend(Object extension) {
		assert !extensions.containsKey(extension.getClass());
		
		extensions.put(extension.getClass(), extension);
	}
	
	@Override
	public <T> T extension(Class<T> type) {
		return (T) extensions.get(type);
	}

}
