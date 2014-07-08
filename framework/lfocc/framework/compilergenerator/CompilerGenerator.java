package lfocc.framework.compilergenerator;

import lfocc.framework.compilergenerator.parsergenerator.ParserGenerator;
import lfocc.framework.feature.FrameworkInterface;

/**
 *
 */
public interface CompilerGenerator extends FrameworkInterface {
	public ParserGenerator getParserGenerator();
}
