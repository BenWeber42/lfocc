package lfocc.framework.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FileSystem {
	
	public static Path createFolder(String folder) throws IOException {
		return Files.createDirectories((new File(folder)).toPath());
	}
	
	public static void writeTo(String src, String destination) throws IOException {
		File dest = new File(destination);
		if (!dest.exists())
			dest.createNewFile();
		
		Writer writer = new BufferedWriter(new FileWriter(dest));
		writer.write(src);
		writer.close();
	}
	
	/**
	 * Copies a single file `from` to `to.
	 * 
	 * Note: If `to` is a folder, the file will be copied into that folder.
	 */
	public static boolean copy(String from, String to, boolean replace) {
		try {
			File _to = new File(to);

			if (_to.isDirectory()) {
				_to = new File(_to, (new File(from)).getName());
			}
			
			if (replace) {
				Files.copy((new File(from)).toPath(), _to.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} else {
				
				Files.copy((new File(from)).toPath(), _to.toPath());
			}
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Same as copy(from, to, true);
	 * 
	 * Warning: replaces existing files!
	 */
	public static boolean copy(String from, String to) {
		return copy(from, to, true);
	}
	
	/**
	 * Issues copy on all files.
	 */
	public static boolean copyAll(List<String> fromFiles, String toFolder) {
		
		Iterator<String> it = fromFiles.iterator();
		boolean status = true;
		while (it.hasNext()) {
			status &= copy(it.next(), toFolder);
		}
		
		return status;
	}

	/**
	 * Get all files of a folder.
	 */
	public static List<String> getFiles(String folder) {
		File f = new File(folder);
		List<String> gathered = new LinkedList<String>();
		
		Iterator<File> files = Arrays.asList(f.listFiles()).iterator();
		while (files.hasNext()) {
			File file = files.next();
			
			if (!file.isDirectory()) {
				gathered.add(file.getPath());
			}
		}
		return gathered;
	}
	
	/**
	 * Get all files of a folder including files in subfolders and so on.
	 */
	public static List<String> getNestedFiles(String folder) {
		File f = new File(folder);
		List<String> gathered = new LinkedList<String>();
		
		Iterator<File> files = Arrays.asList(f.listFiles()).iterator();
		while (files.hasNext()) {
			File file = files.next();
			
			if (file.isDirectory()) {
				gathered.addAll(getNestedFiles(file.getPath()));
			} else {
				gathered.add(file.getPath());
			}
		}
		return gathered;
	}

}
