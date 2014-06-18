package lfocc.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Command {
	
	/**
	 * Same as execute(command, false);
	 */
	public static boolean execute(String command) {
		return execute(command, false);
	}
	

	/**
	 * Executes `command`
	 * 
	 * @param output whether the output should be logged (with info level)
	 */
	public static boolean execute(String command, boolean output) {

		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec(command);
		} catch (IOException e1) {
			Logger.warning("Failed to execute:");
			Logger.warning(command);
			return false;
		}

		boolean finished = false;

		while (!finished) {
			try {
				proc.waitFor();
				finished = true;
			} catch (InterruptedException e) {
			}
		}
		
		if (output) {
			List<String> lines = toStringList(proc.getInputStream());
			Iterator<String> it = lines.iterator();
			while (it.hasNext()) {
				Logger.info(it.next());
			}
		}

		if (proc.exitValue() != 0) {
			Logger.warning("Failed to execute:");
			Logger.warning(command);
			Logger.warning("ErrorStream:");
			
			List<String> lines = toStringList(proc.getErrorStream());
			Iterator<String> it = lines.iterator();
			while (it.hasNext()) {
				Logger.warning(it.next());
			}
			
			return false;
		}

		return true;
	}
	
	private static List<String> toStringList(InputStream out) {
		List<String> output = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new InputStreamReader(out));

		try {
			String line = in.readLine();
			while (line != null) {
				output.add(line);
				line = in.readLine();
			}
		} catch (IOException e) {
			
		}
		
		return output;
	}
}
