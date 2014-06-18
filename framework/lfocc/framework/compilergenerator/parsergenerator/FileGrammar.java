package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileGrammar extends Grammar {
	
	private File file;

	public FileGrammar(String feature, File grammar, String grammarName) {
		name = grammarName;
		this.feature = feature;
		file = grammar;
	}

	@Override
	public void copyTo(File destination) throws IOException {
		Files.copy(file.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

}
