package lfocc.framework.compilergenerator;


public interface SemanticsGenerator {
	public void addTransformer(int priority, String packageName, String transformer);
}
