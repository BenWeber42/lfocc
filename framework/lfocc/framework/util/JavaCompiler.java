package lfocc.framework.util;

import java.io.File;
import java.util.Iterator;

public class JavaCompiler {

	public static boolean compile(String classPath, File outputFolder, File file) {
		return Command.execute("javac" +
				" -cp " + classPath + 
				" -d " + outputFolder.getPath() +
				" " + file.getPath());
	}
	
	/**
	 * Compiles a complete folder and all sub folder recursively!
	 * 
	 * Warning: continues on error!
	 */
	public static boolean compileFolder(String classPath, File outputFolder, File folder) {
		
		StringBuilder sb = new StringBuilder("javac" +
				" -cp " + classPath + 
				" -d " + outputFolder.getPath());

		Iterator<String> it = FileSystem.getNestedFiles(folder.getPath()).iterator();
		while (it.hasNext()) {
			String file = it.next();
			if (file.endsWith(".java"))
				sb.append(" " + file);
		}
		
		return Command.execute(sb.toString());
	}
}
