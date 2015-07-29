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
	public static void copy(String from, String to, boolean replace) throws IOException {
		File _to = new File(to);
		File _from = new File(from);

		if (_to.isDirectory()) {
			_to = new File(_to, _from.getName());
		}
		
		if (replace) {
			Files.copy(_from.toPath(), _to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} else {
			
			Files.copy(_from.toPath(), _to.toPath());
		}
	}
	
	/**
	 * Same as copy(from, to, true);
	 * 
	 * Warning: replaces existing files!
	 */
	public static void copy(String from, String to) throws IOException {
		copy(from, to, true);
	}
	
	/**
	 * Issues copy on all files.
	 * @throws IOException 
	 */
	public static boolean copyAll(List<String> fromFiles, String toFolder) throws IOException {
		
		Iterator<String> it = fromFiles.iterator();
		boolean status = true;
		while (it.hasNext()) {
			copy(it.next(), toFolder);
		}
		
		return status;
	}
	
	/**
	 * Moves file from `target` to `destination`
	 */ 
	public static void move(String target, String destination, boolean replace) throws IOException {
		File from = new File(target);
		File to = new File(destination);
		
		if (to.isDirectory())
			to = new File(to, from.getName());
		
		if (replace)
			Files.move(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		else
			Files.move(from.toPath(), to.toPath());
	}
	
	/**
	 * Same as move(target, destination, true)
	 */
	public static void move(String target, String destination) throws IOException {
		move(target, destination, true);
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
		
		if (f.listFiles() == null)
			return gathered;
		
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
