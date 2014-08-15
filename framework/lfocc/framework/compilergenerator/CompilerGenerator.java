package lfocc.framework.compilergenerator;

import java.io.File;

import lfocc.framework.feature.FrameworkInterface;

public interface CompilerGenerator extends FrameworkInterface {
	public ParserGenerator getParserGenerator();
	public SemanticsGenerator getSemanticsGenerator();
	public void addSource(String _package, File file);
}
