package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lfocc.framework.util.Command;
import lfocc.framework.util.Logger;


public class ParserGenerator {
	
	String root = null;
	List<Grammar> parserGrammars = new ArrayList<Grammar>();
	List<Grammar> treeGrammars = new ArrayList<Grammar>();
	Set<String> tokens = new HashSet<String>();
	
	public void addParserGrammar(Grammar grammar) {
		parserGrammars.add(grammar);
	}

	public void addParserGrammar(String feature, String grammar, String grammarName) {
		parserGrammars.add(new StringGrammar(feature, grammar, grammarName));
	}

	public void addParserGrammar(String feature, File grammar, String grammarName) {
		parserGrammars.add(new FileGrammar(feature, grammar, grammarName));
	}
	
	public void addTreeGrammar(Grammar grammar) {
		treeGrammars.add(grammar);
	}

	public void addTreeGrammar(String feature, String grammar, String grammarName) {
		treeGrammars.add(new StringGrammar(feature, grammar, grammarName));
	}

	public void addTreeGrammar(String feature, File grammar, String grammarName) {
		treeGrammars.add(new FileGrammar(feature, grammar, grammarName));
	}
	
	public void addToken(String token) {
		tokens.add(token);
	}
	
	public boolean hasRootRule() {
		return root != null;
	}

	public void setRootRule(String root) {
		this.root = root;
	}
	
	/**
	 * Copies all grammars to `path` and creates the root grammar there too.
	 */
	public void copyTo(File path, String languageName) throws IOException {

		Grammar root = new StringGrammar("framework", generateRootParser(languageName), "Root");
		File dest = new File(path, root.getName() + ".g");

		try {
			root.copyTo(dest);
		} catch (IOException e) {
			Logger.error("Failed to copy parser grammar '%s' to '%s' during parser generating!",
					root.getName(), dest.toString());
			throw e;
		}

		Grammar rootWalker = new StringGrammar("framework", generateRootWalker(languageName), "RootWalker");
		dest = new File(path, rootWalker.getName() + ".g");

		try {
			rootWalker.copyTo(dest);
		} catch (IOException e) {
			Logger.error("Failed to copy tree grammar '%s' to '%s' during parser generating!",
					rootWalker.getName(), dest.toString());
			throw e;
		}

		// copy all parser grammars to the output path
		Iterator<Grammar> it = parserGrammars.iterator();
		while (it.hasNext()) {
			Grammar grammar = it.next();
			dest = new File(path, grammar.getName() + ".g");
			try {
				grammar.copyTo(dest);
			} catch (IOException e) {
				Logger.error("Failed to copy parser grammar '%s' to '%s' during parser generating!",
						grammar.getName(), dest.toString());
				throw e;
			}
		}

		// copy all tree grammars to the output path
		it = treeGrammars.iterator();
		while (it.hasNext()) {
			Grammar grammar = it.next();
			dest = new File(path, grammar.getName() + ".g");
			try {
				grammar.copyTo(dest);
			} catch (IOException e) {
				Logger.error("Failed to copy tree grammar '%s' to '%s' during parser generating!",
						grammar.getName(), dest.toString());
				throw e;
			}
		}
	}
	
	public boolean generate(File path) {

		// Process with antlr: 
		return Command.execute(
				"java -jar ./lib/antlr-3.4.jar" + 
				" -fo " + path.getPath() +
				" " + path.getPath() + "/Root.g"/* +
				" " + path.getPath() + "/RootWalker.g"*/
				);
		
	}
	
	private String generateRootParser(String languageName) {
		String grammar =
				"grammar Root;\n\n";
		
		// imports:
		Iterator<Grammar> it = parserGrammars.iterator();
		if (it.hasNext()) {
			grammar += "import \n"; 
			grammar += "   " + it.next().getName();

			while (it.hasNext())
				grammar +=
						",\n   " + it.next().getName();
	
			grammar +=
					"\n;\n\n";
		}
		
		grammar +=
				"tokens {\n";
		
		Iterator<String> token = tokens.iterator();
		while (token.hasNext())
			grammar += "   " + token.next() + ";\n";
				
		grammar += "}\n\n";

		// settings:
		grammar += 
				"@header {\n" +
				"package lfocc.compilers." + languageName + ".parser;\n" +
				"}\n\n";
				
		grammar += 
				"@lexer::header {\n" +
				"package lfocc.compilers." + languageName + ".parser;\n" +
				"}\n\n";
			
		// root rule:
		grammar += "root : " + root + " EOF ;";
		
		return grammar;
	}
	
	private String generateRootWalker(String languageName) {
		String grammar = "";

		grammar += "tree grammar RootWalker;\n";
		grammar += "\n";

		Iterator<Grammar> it = treeGrammars.iterator();
		if (it.hasNext()) {
			grammar += "import \n";
			grammar += "   " + it.next().getName();

			while (it.hasNext())
				grammar += ",\n   " + it.next().getName();

			grammar += ";\n";
			grammar += "\n";
		}
		
		grammar += "options {\n";
		grammar += "   tokenVocab=Root;\n";
		grammar += "   ASTLabelType=CommonTree;\n";
		grammar += "}\n";
		grammar += "\n";
		grammar += "@header {\n";
		grammar += "package lfocc.compilers." + languageName + ".parser;\n";
		grammar += "\n";
		grammar += "import lfocc.framework.compiler.ir.ASTNode;\n";
		grammar += "}\n";
		grammar += "\n";
		grammar += "root returns [ASTNode root]\n";
		grammar += "   : " + root + "=" + root + " { $root = $" + root + "." + root + " }\n";
		grammar += "   ;\n";
		grammar += "\n";
		grammar += "\n";
		grammar += "\n";
		grammar += "\n";
		
		return grammar;
	}
}
