package lfocc.framework.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class JavaCompiler {

	public static boolean compile(String classPath, File outputFolder, File file) {
		return Command.execute(new String[]{"javac",
				// adds debugging information for remote debugging
				"-g:source,lines,vars",
				"-cp " + classPath,
				"-d " + outputFolder.getPath(),
				file.getPath()});
	}
	
	/**
	 * Compiles a complete folder and all sub folder recursively!
	 * 
	 * Warning: continues on error!
	 */
	public static boolean compileFolder(String classPath, File outputFolder, File folder) {
		
		List<String> command = new ArrayList<String>(Arrays.asList("javac",
				// adds debugging information for remote debugging
				"-g:source,lines,vars",
				"-cp", classPath,
				"-d", outputFolder.getPath()));

		Iterator<String> it = FileSystem.getNestedFiles(folder.getPath()).iterator();
		while (it.hasNext()) {
			String file = it.next();
			if (file.endsWith(".java"))
				command.add(file);
		}
		
		return Command.execute(command.toArray(new String[command.size()]));
	}
}
