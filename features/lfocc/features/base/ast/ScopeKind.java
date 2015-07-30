package lfocc.features.base.ast;

/**
 * Marker from which scope kind an AST node is (only relevant for variables
 * and functions).
 */
public enum ScopeKind {
	GLOBAL,
    CLASS_MEMBER,
    LOCAL // includes also parameters
}