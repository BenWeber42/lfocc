package lfocc.features.syntaxbase;

import java.io.File;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;

public class SyntaxBase extends Feature {
	
	private static final String SYNTAX_BASE_LEXER_NAME = "BaseLexer";
	private static final String SYNTAX_BASE_LEXER_FILE =
			"features/lfocc/features/syntaxbase/BaseLexer.g";

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserGrammar(
				getName(), new File(SYNTAX_BASE_LEXER_FILE), SYNTAX_BASE_LEXER_NAME);
	}

}
