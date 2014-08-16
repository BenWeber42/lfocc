package lfocc.features.types.ast;

public abstract class TypeSymbol {
	
	public abstract String getName();

	public boolean equals(TypeSymbol other) {
		return getName().equals(other.getName());
	}
}
