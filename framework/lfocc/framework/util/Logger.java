package lfocc.framework.util;

public class Logger {
	
	private static boolean debug = false;
	
	public static boolean getDebug() {
		return debug;
	}
	
	public static void setDebug(boolean d) {
		debug = d;
	}

	public static void debug(String msg) {
		if (debug)
			System.out.println(String.format("Debug: %s", msg));
	}

	public static void info(String msg) {
		System.out.println(String.format("Info: %s", msg));
	}

	public static void warning(String msg) {
		System.out.println(String.format("Warning: %s", msg));
	}

	public static void error(String msg) {
		System.out.println(String.format("Error: %s", msg));
	}
	
	public static void fatal(String msg) {
		System.out.println(String.format("Fatal: %s", msg));
	}
	
	public static void debug(String msg, Object... args) {
		if (debug)
			System.out.println(String.format("Debug: %s", String.format(msg, args)));
	}
	public static void info(String msg, Object... args) {
		System.out.println(String.format("Info: %s", String.format(msg, args)));
	}

	public static void warning(String msg, Object... args) {
		System.out.println(String.format("Warning: %s", String.format(msg, args)));
	}

	public static void error(String msg, Object... args) {
		System.out.println(String.format("Error: %s", String.format(msg, args)));
	}
	
	public static void fatal(String msg, Object... args) {
		System.out.println(String.format("Fatal: %s", String.format(msg, args)));
	}
}
