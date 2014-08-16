package lfocc.features.base;

import java.io.File;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;

public class Base extends Feature {
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		// -1 do decrease the priority of identifier below default
		cg.getParserGenerator().addToken("identifier", "String", "/[a-zA-Z][a-zA-Z0-9_]*/ -1   { $lexem = current(); break; }");
		cg.getParserGenerator().addToken("'{'", "/\\{/");
		cg.getParserGenerator().addToken("'}'", "/\\}/");
		cg.getParserGenerator().addToken("'('", "/\\(/");
		cg.getParserGenerator().addToken("')'", "/\\)/");
		cg.getParserGenerator().addToken("';'", "/;/");
		cg.getParserGenerator().addToken("','", "/,/");
		cg.getParserGenerator().addToken("whitespace", "/[\\n\\r\\t ]+/ { return false; }");
		// TODO: add configuration
		// parses C++ style one line comments ( // ... )
		cg.getParserGenerator().addToken("single_line_comment", "/\\/\\/[^\\n]*/ { return false; }");
		// FIXME: parse /*/*/ as multi_line_comment
		// parses C++ style multi line comments ( /* .. */ ), can't parse /***/
		cg.getParserGenerator().addToken("multi_line_comment", "/\\/\\*+((([^\\*])+)|([\\*]+(?!\\/)))[*]+\\// { return false; }");
		// parses C++ style multi line comments special case /****/
		cg.getParserGenerator().addToken("multi_line_comment2", "/\\/\\*[*]+\\// { return false; }");
		
		cg.getParserGenerator().addImport("java.util.List");
		cg.getParserGenerator().addImport("java.util.ArrayList");
		cg.getParserGenerator().addImport("java.util.Arrays");
		cg.getParserGenerator().addImport("java.util.Iterator");
		cg.getParserGenerator().addImport("lfocc.framework.compiler.ast.*");
		
		if (!cg.hasFeature("Types")) {
			/*
			 * Even without static typing the TypeSymbol interface is required
			 * by many AST nodes and features.
			 */
			cg.addSource("lfocc.features.types.ast",
					new File("features/lfocc/features/types/ast/TypeSymbol.java"));
		}
	}
}
