package lfocc.features.classes.ast;

import java.util.HashMap;
import java.util.Map;

import lfocc.features.types.ast.TypeSymbol;

public class ClassType extends TypeSymbol {

	private String name;
	private ClassType parent = null;
	private Map<String, Object> members = new HashMap<String, Object>();
	private ClassDeclaration node;
	
	public ClassType(ClassDeclaration node) {
		this.name = node.getName();
		this.node = node;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ClassDeclaration getNode() {
		return node;
	}

	public ClassType getParent() {
		return parent;
	}

	public void setParent(ClassType parent) {
		this.parent = parent;
	}
	
	public void addMember(String member, Object obj) {
		members.put(name, obj);
	}
	
	public Object getMember(String member) {
		return members.get(member);
	}

}
