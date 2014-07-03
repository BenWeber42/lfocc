package lfocc.framework.feature;

import java.util.HashSet;
import java.util.Set;

public abstract class SyntaxExtendable extends Feature {

	protected Set<String> rules = new HashSet<String>();

	public void addSyntaxRule(String rule) {
		rules.add(rule);
	}
}
