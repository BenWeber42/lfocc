package lfocc.framework.compilergenerator.parsergenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StringGrammar extends Grammar {
	
	private String grammar;
	
	public StringGrammar(String feature, String grammar, String grammarName) {
		this.feature = feature;
		name = grammarName;
		this.grammar = grammar;
	}

	@Override
	public void copyTo(File destination) throws IOException {
		if (!destination.exists())
			destination.createNewFile();
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(destination));
		writer.write(grammar);
		writer.close();
	}
}
