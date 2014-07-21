package lfocc.features.syntaxbase;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;

public class SyntaxBase extends Feature {
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		// -1 do decrease the priority of identifier below default
		cg.getParserGenerator().addToken("identifier", "/[a-zA-Z][a-zA-Z0-9_]*/ -1");
		cg.getParserGenerator().addToken("'{'", "/\\{/");
		cg.getParserGenerator().addToken("'}'", "/\\}/");
		cg.getParserGenerator().addToken("'('", "/\\(/");
		cg.getParserGenerator().addToken("')'", "/\\)/");
		cg.getParserGenerator().addToken("';'", "/;/");
		cg.getParserGenerator().addToken("','", "/,/");
		cg.getParserGenerator().addToken("whitespace", "/[\\n\\r\\t ]+/ { return false; }");
		// TODO: add configuration
		// FIXME: allow multi line comments to parse '/************/' and '/**/'
		cg.getParserGenerator().addToken("multi_line_comment", "/\\/\\*+((([^\\*])+)|([\\*]+(?!\\/)))[*]+\\// { return false; }");
		cg.getParserGenerator().addToken("single_line_comment", "/\\/\\/[^\\n]*/ { return false; }");
	}
}
