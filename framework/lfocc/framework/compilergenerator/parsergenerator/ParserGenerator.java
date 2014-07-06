package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lfocc.framework.util.Command;
import lfocc.framework.util.FileSystem;


public class ParserGenerator {
	
	String root = null;
	Map<String, List<String>> parserGrammar = new HashMap<String, List<String>>();
	Map<String, List<String>> walkerGrammar = new HashMap<String, List<String>>();
	Set<String> tokens = new HashSet<String>();
	String name;
	
	public ParserGenerator(String name) {
		this.name = name;
	}

	public void addParserSource(String feature, String source) {
		if (parserGrammar.get(feature) == null)
			parserGrammar.put(feature, new ArrayList<String>());

		parserGrammar.get(feature).add(source);
	}

	public void addWalkerSource(String feature, String source) {
		if (walkerGrammar.get(feature) == null)
			walkerGrammar.put(feature, new ArrayList<String>());

		walkerGrammar.get(feature).add(source);
	}

	public void addParserToken(String token) {
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
	public void copyTo(File path) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		Iterator<String> it = parserGrammar.keySet().iterator();
		while (it.hasNext()) {
			String feature = it.next();
			Iterator<String> src = parserGrammar.get(feature).iterator();
			
			if (src.hasNext()) {
				sb.append(String.format(
						"\n" +
						"///////////////////////////////////////////////////\n" +
						"// Added by feature '%s'\n" +
						"//////////////////////////////////////////////////\n\n\n",
						feature));
				
				while (src.hasNext()) {
					sb.append(src.next());
					sb.append("\n");
				}
			}
		}

		sb.insert(0, generateRootParser());
		FileSystem.writeTo(sb.toString(), path.toString() + "/" + name + ".g");
		
		sb = new StringBuilder();

		it = walkerGrammar.keySet().iterator();
		while (it.hasNext()) {
			String feature = it.next();
			Iterator<String> src = walkerGrammar.get(feature).iterator();
			
			if (src.hasNext()) {
				sb.append(String.format(
						"\n" +
						"///////////////////////////////////////////////////\n" +
						"// Added by feature '%s'\n" +
						"//////////////////////////////////////////////////\n\n\n",
						feature));
				
				while (src.hasNext()) {
					sb.append(src.next());
					sb.append("\n");
				}
			}
		}
		
		sb.insert(0, generateRootWalker());
		FileSystem.writeTo(parserGrammar.toString(), path.toString() + "/" + name + "Walker.g");
		
	}
	
	public boolean generate(File path) {

		// Process with antlr: 
		return Command.execute(
				"java -jar ./lib/antlr-3.4.jar" + 
				" -fo " + path.getPath() +
				" " + path.getPath() + "/" + name + ".g"/* +
				" " + path.getPath() + "/" + name + "Walker.g"*/
				);
		
	}
	
	private String generateRootParser() {
		String src =
				"grammar " + name + ";\n\n";
		
		src +=
				"tokens {\n";
		
		Iterator<String> token = tokens.iterator();
		while (token.hasNext())
			src += "   " + token.next() + ";\n";
				
		src += "}\n\n";

		// settings:
		src += 
				"@header {\n" +
				"package lfocc.compilers." + name + ".parser;\n" +
				"}\n\n";
				
		src += 
				"@lexer::header {\n" +
				"package lfocc.compilers." + name + ".parser;\n" +
				"}\n\n";
			
		// root rule:
		src += "root : " + root + " ;";
		src += "\n";
		src += "\n";
		
		return src;
	}
	
	private String generateRootWalker() {
		String src = "";

		src += "tree grammar RootWalker;\n";
		src += "\n";
		
		src += "options {\n";
		src += "   tokenVocab=Root;\n";
		src += "   ASTLabelType=CommonTree;\n";
		src += "}\n";
		src += "\n";
		src += "@header {\n";
		src += "package lfocc.compilers." + name + ".parser;\n";
		src += "\n";
		src += "import lfocc.framework.compiler.ir.ASTNode;\n";
		src += "}\n";
		src += "\n";
		src += "root returns [ASTNode root]\n";
		src += "   : " + root + "=" + root + " { $root = $" + root + "." + root + " }\n";
		src += "   ;\n";
		src += "\n";
		src += "\n";
		src += "\n";
		src += "\n";
		
		return src;
	}
}
