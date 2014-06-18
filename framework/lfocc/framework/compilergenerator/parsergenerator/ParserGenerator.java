package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lfocc.framework.util.Command;
import lfocc.framework.util.Logger;


public class ParserGenerator {
	
	String root = null;
	List<Grammar> grammars = new ArrayList<Grammar>();
	
	public void addGrammar(String feature, String grammar, String grammarName) {
		grammars.add(new StringGrammar(feature, grammar, grammarName));
	}

	public void addGrammar(String feature, File grammar, String grammarName) {
		grammars.add(new FileGrammar(feature, grammar, grammarName));
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

		///////////////////////////////////////////////////////////////////////
		// Create Root gramma:
		///////////////////////////////////////////////////////////////////////

		// grammar name:
		String rootGrammar =
				"grammar Root;\n\n";
		
		// imports:
		rootGrammar += "import \n"; 
		
		Iterator<Grammar> it = grammars.iterator();
		if (it.hasNext())
			rootGrammar += "   " + it.next().getName();
		while (it.hasNext()) {
			rootGrammar +=
					",\n   " + it.next().getName();
		}

		rootGrammar +=
				"\n;\n\n";

		// settings:
		rootGrammar += 
				"@header {\n" +
				"package lfocc.compilers." + languageName + ".parser;\n" +
				"}\n\n";
				
		rootGrammar += 
				"@lexer::header {\n" +
				"package lfocc.compilers." + languageName + ".parser;\n" +
				"}\n\n";
			
		// root rule:
		rootGrammar += "root : " + root + ";\n\n";
		
		///////////////////////////////////////////////////////////////////////
		// Copy grammars to path
		///////////////////////////////////////////////////////////////////////
		
		Grammar root = new StringGrammar("framework", rootGrammar, "Root");
		File dest = new File(path, root.getName() + ".g");

		try {
			root.copyTo(dest);
		} catch (IOException e) {
			Logger.error("Failed to copy grammar '%s' to '%s' during parser generating!",
					root.getName(), dest.toString());
			throw e;
		}

		// copy all grammars to the output path
		it = grammars.iterator();
		while (it.hasNext()) {
			Grammar grammar = it.next();
			dest = new File(path, grammar.getName() + ".g");
			try {
				grammar.copyTo(dest);
			} catch (IOException e) {
				Logger.error("Failed to copy grammar '%s' to '%s' during parser generating!",
						grammar.getName(), dest.toString());
				throw e;
			}
		}
	}
	
	public boolean generate(File path) {

		// Process with antlr: 
		return Command.execute("java -jar ./lib/antlr-3.4.jar " + path.getPath() + "/Root.g");
		
	}
}
