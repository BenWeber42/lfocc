package lfocc.framework.compilergenerator.options;

public class Options {

	/*
	 * If needed the following methods could be added:
	 * - public void addFlag(String flag)
	 * - public void addOption(String name);
	 */
	
	/**
	 * Generates a command line arguments parser for a compiler to be generated
	 */
	public String generate(String language) {
		String src = "";
		
		src += String.format("package lfocc.compilers.%s.options", language) + ";\n";
		src += "\n";
		src += "public class Options {\n";

		///////////////////////////////////////////////////////////////////////
		// Attributes
		///////////////////////////////////////////////////////////////////////
		src += "   private String input;\n";
		src += "   private String output;\n";

		///////////////////////////////////////////////////////////////////////
		// Constructor
		///////////////////////////////////////////////////////////////////////
		src += "   public Options(String[] args) {\n";
		src += "      if (args.length < 2) {\n";
		src += "         System.out.println(\"Not enough arguments!\");\n";
		src += "         usage();\n";
		src += "         System.exit(-1);\n";
		src += "      }\n";
		src += "      this.input = args[0];\n";
		src += "      this.output = args[1];\n";
		src += "   }\n";
		src += "\n";

		///////////////////////////////////////////////////////////////////////
		// String getInput()
		///////////////////////////////////////////////////////////////////////
		src += "   public String getInput() {\n";
		src += "      return input;\n";
		src += "   }\n";
		src += "\n";

		///////////////////////////////////////////////////////////////////////
		// String getOutput()
		///////////////////////////////////////////////////////////////////////
		src += "   public String getOutput() {\n";
		src += "      return output;\n";
		src += "   }\n";
		src += "\n";

		///////////////////////////////////////////////////////////////////////
		// void usage()
		///////////////////////////////////////////////////////////////////////
		src += "   public void usage() {\n";
		src += "      System.out.println(\"Usage: <" + language + "> <input> <output>\");\n";
		src += "   }\n";
		src += "}\n";

		return src;
	}
}
