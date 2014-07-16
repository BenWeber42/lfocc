package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lfocc.framework.util.Command;
import lfocc.framework.util.FileSystem;


public class ParserGenerator {
	
	String root = null;
	Map<String, List<String>> parserGrammar = new HashMap<String, List<String>>();
	Map<String, String> tokens = new HashMap<String, String>();
	Map<Integer, List<String>> precedence = new TreeMap<Integer, List<String>>();
	String name;
	
	public ParserGenerator(String name) {
		this.name = name;
	}

	public void addParserSource(String feature, String source) {
		if (parserGrammar.get(feature) == null)
			parserGrammar.put(feature, new ArrayList<String>());

		parserGrammar.get(feature).add(source);
	}

	public void addToken(String name, String regex) {
		assert !tokens.containsKey(name);
		
		// TODO: allow to specify a return value
		
		tokens.put(name, regex);
	}
	
	public void addPrecedence(String name, int level) {
		if (!precedence.containsKey(level))
			precedence.put(level, new ArrayList<String>());
		
		precedence.get(level).add(name);
	}
	
	public boolean hasRootRule() {
		return root != null;
	}

	public void setRootRule(String root) {
		assert this.root == null;

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
						"############################################################\n" +
						"# Added by feature '%s'\n" +
						"############################################################\n\n\n",
						feature));
				
				while (src.hasNext()) {
					sb.append(src.next());
					sb.append("\n");
				}
			}
		}

		sb.insert(0, generateRootParser());
		FileSystem.writeTo(sb.toString(), path.toString() + "/" + name + ".s");
		
	}
	
	public boolean generate(File path) {

		// Process with lapg: 
		// TODO: lapg doesn't seem to respect the -o flag
		// TODO: abort on conflict warnings
		return Command.execute(
				"java -jar ./lib/lapg-1.3.10.jar" + 
				" -o " + path.getPath() +
				" " + path.getPath() + "/" + name + ".s"
				);
		
	}
	
	private String generateRootParser() {
		String src = "";

		src += "############################################################\n";
		src += "# " + name + " grammar\n";
		src += "############################################################\n";
		src += "\n";
		src += "# Configuration\n";
		src += "\n";
		src += "prefix = \"" + name + "\"\n";
		src += "lang = \"java\"\n";
		src += "\n";
		src += "# Tokens\n";
		src += "\n";
		
		Iterator<Map.Entry<String, String>> it = tokens.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> token = it.next();
			src += String.format("%-10s:      %s\n", token.getKey(), token.getValue());
		}

		src += "\n";
		src += "# Grammar\n";
		src += "\n";
		
		Iterator<List<String>> _it = precedence.values().iterator();
		while (_it.hasNext()) {
			src += "%left";

			Iterator<String> __it = _it.next().iterator();
			while (__it.hasNext())
				src += " " + __it.next() + " ";

			src += ";\n";
		}

		src += "\n";
		src += "input ::= " +  root + " ;\n";
		src += "\n";
		
		return src;
		
	}
}
