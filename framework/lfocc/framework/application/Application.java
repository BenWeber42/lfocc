package lfocc.framework.application;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import lfocc.framework.compilergenerator.BackendGenerator;
import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.Options;
import lfocc.framework.compilergenerator.ParserGenerator;
import lfocc.framework.compilergenerator.SemanticsGenerator;
import lfocc.framework.config.GlobalConfiguration;
import lfocc.framework.config.LanguageConfigurationLoader;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FeatureLoader;
import lfocc.framework.feature.service.Service;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.util.FileSystem;
import lfocc.framework.util.JavaCompiler;
import lfocc.framework.util.Logger;

public class Application implements SemanticsGenerator, CompilerGenerator, BackendGenerator, FeatureHelper, ServiceProvider {
	
	private String[] args;
	private Map<String, Feature> features = new HashMap<String, Feature>();
	private LanguageConfigurationLoader configLoader = new LanguageConfigurationLoader();
	private GlobalConfiguration cfg;
	private Map<String, Map<String, Service>> services = new HashMap<String, Map<String, Service>>();
	private ParserGenerator parserGenerator;
 	private File outputFolder = null;
	private File srcFolder = null;
	private File packageFolder = null;
	private File grammarFolder = null;
	private File binFolder = null;
	private String currentFeature = null; // relevant for the FeatureHelper interface
	private List<String> currentFeatureConfiguration = null;
	private Options options = new Options();
	private List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
	private List<SourceCode> sourceCodes = new ArrayList<SourceCode>();
	private Map<Integer, String> semanticsPackages = new TreeMap<Integer, String>();
	private Map<Integer, String> semanticsNames = new TreeMap<Integer, String>();
	private String backend = null;
	private String backendPackage = null;
	
	public Application(String[] args) {
		this.args = args;
	}

	public static void printUsage() {
		System.out.println("Usage: lfocc <langFile> [args]");
		System.out.println();
		System.out.println("      <langFile>      LanguageConfiguration file specifying the language.");
	}
	
	private void loadConfig() {

		if (args.length < 1) {
			Logger.error("No LanguageConfiguration file given!");
			printUsage();
			exit(-1);
		}

		configLoader.process(new File(args[0]));
		cfg = configLoader.getGlobalConfiguration();
		parserGenerator = new ParserGenerator(cfg.name());

	}
	
	private void loadFeatures() {
		// TODO: put 'bin' path into global configuration
		FeatureLoader loader = new FeatureLoader("bin");
		try {
			loader.load(configLoader.getFeatures());
		} catch (ClassNotFoundException e) {
			Logger.error("Failed to load features!");
			e.printStackTrace();
			exit(-1);
		} catch (InstantiationException e) {
			Logger.error("Failed to load features!");
			e.printStackTrace();
			exit(-1);
		} catch (IllegalAccessException e) {
			Logger.error("Failed to load features!");
			e.printStackTrace();
			exit(-1);
		}
		
		features = loader.getAllFeatures();
	}
	
	private void setupFeatureArrangements() {
		Iterator<Feature> it = features.values().iterator();
		while (it.hasNext()) {
			it.next().setupFeatureArrangements(this);
		}
	}

	private void setupFeatures() {
		Iterator<Feature> it = features.values().iterator();
		while (it.hasNext()) {
			Feature feature = it.next();
			currentFeature = feature.getName();
			currentFeatureConfiguration = null;
			feature.setup(this);
			
			Logger.info(String.format("Successfully setup feature '%s' with configuration: ", feature.getName()));
			
			if (currentFeatureConfiguration == null) {
				Logger.info("- No configuration available.");
				continue;
			}

			Iterator<String> config = currentFeatureConfiguration.iterator();

			while (config.hasNext()) {
				Logger.info(String.format("- %s", config.next()));
			}
		}
	}
	
	private void setupOutputFolder() {
		// TODO: parameterize output folders
		try {
			outputFolder = FileSystem.createFolder("compilers/" +
					cfg.name()
					+ "/").toFile();
			srcFolder = FileSystem.createFolder(outputFolder.getPath() + "/src/").toFile();
			packageFolder = new File(srcFolder, "/lfocc/compilers/" + cfg.name() + "/");
			FileSystem.createFolder(packageFolder.getPath() + "/application/").toFile();
			FileSystem.createFolder(packageFolder.getPath() + "/options/").toFile();
			FileSystem.createFolder(packageFolder.getPath() + "/parser/").toFile();
			grammarFolder = FileSystem.createFolder(outputFolder.getPath() + "/grammar").toFile();
			binFolder = FileSystem.createFolder(outputFolder.getPath()+ "/bin").toFile();
		} catch (IOException e) {
			Logger.error("Failed to setup output folder due to");
			Logger.error(e.getMessage());
			exit(-1);
		}
		
	}
	
	private void setupCompilerGenerator() {
		Iterator<Feature> it = features.values().iterator();
		while (it.hasNext()) {
			Feature feature = it.next();
			feature.setupCompilerGenerator(this);
		}
	}
	
	private void generateCompiler() {
		// copy runtime & infrastructure
		try {
			List<String> runtime = FileSystem.getNestedFiles("framework/lfocc/framework/compiler/");
			runtime.addAll(FileSystem.getNestedFiles("framework/lfocc/framework/util/"));
			Iterator<String> r = runtime.iterator();
			while (r.hasNext()) {
				String file = r.next();
				String target = srcFolder.getPath() + "/lfocc/framework/compiler/" +
						file.replace("framework/lfocc/framework/compiler/", "");
				String folder = (new File(target)).getParent();
				FileSystem.createFolder(folder);
				FileSystem.copy(file, target);
			}
		} catch (IOException e2) {
			Logger.error("Failed to copy compiler runtime to output folder!");
			e2.printStackTrace();
			exit(-1);
		}
			
		
		// generate files:
		try {
			FileSystem.writeTo(generateMainFile(), packageFolder.getPath() + "/Main.java");
			FileSystem.writeTo(generateApplicationFile(), packageFolder.getPath() + "/application/Application.java");
			FileSystem.writeTo(options.generate(cfg.name()), packageFolder.getPath() + "/options/Options.java");
		} catch (IOException e1) {
			Logger.error("Failed to write main files!");
			e1.printStackTrace();
			exit(-1);
		}
		
		// copy sources:
		try {
			Iterator<SourceFile> s = sourceFiles.iterator();
			while (s.hasNext()) {
				SourceFile source = s.next();
				String folder = srcFolder.toPath() + "/" + source.getPackage().replace(".", "/") + "/";
				FileSystem.createFolder(folder);
				FileSystem.copy(source.getFile().getPath(), folder + source.getFile().getName());
			}
		} catch (IOException e) {
			Logger.error("Failed to copy source files to output folder!");
			e.printStackTrace();
			exit(-1);
		}
		
		try {
			Iterator<SourceCode> s = sourceCodes.iterator();
			while  (s.hasNext()) {
				SourceCode source = s.next();
				String folder = srcFolder.toPath() + "/" + source.getPackage().replace(".", "/") + "/"; 
				FileSystem.createFolder(folder);
				FileSystem.writeTo(source.getCode(), folder + "/" + source.getName() + ".java");
			}
		} catch (IOException e) {
			Logger.error("Failed to copy source files to output folder!");
			e.printStackTrace();
			exit(-1);
		}
		
		// parser generator:
		try {
			parserGenerator.copyTo(grammarFolder);
		} catch (IOException e) {
			Logger.error("Failed to copy parser grammar to output folder!");
			e.printStackTrace();
			exit(-1);
		}
		if (!parserGenerator.generate(grammarFolder, new File(packageFolder, "/parser/"))) {
			Logger.error("Failed to generate parser!");
			exit(-1);
		}
	}
	
	private String generateMainFile() {
		String src = "";

		src += "package lfocc.compilers." + cfg.name() + ";\n";
		src += "\n";
		src += "import lfocc.compilers." + cfg.name() + ".application.Application;\n";
		src += "\n";
		src += "public class Main {\n";
		src += "\n";
		src += "   public static void main(String[] args) {\n";
		src += "   Application app = new Application(args);\n";
		src += "   app.run();\n";
		src += "   }\n";
		src += "}\n";
		
		return src;
	}
	
	private String generateApplicationFile() {
		String src = "";
		
		src += "package lfocc.compilers." + cfg.name() + ".application;\n";
		src += "\n";

		///////////////////////////////////////////////////////////////////////
		// Imports
		///////////////////////////////////////////////////////////////////////
		src += "import java.io.Reader;\n";
		src += "import java.io.InputStreamReader;\n";
		src += "import java.io.FileInputStream;\n";
		src += "import java.io.File;\n";
		src += "import java.io.Writer;\n";
		src += "import java.io.FileWriter;\n";
		src += "import java.io.BufferedWriter;\n";
		src += "import java.io.FileNotFoundException;\n";
		src += "import java.io.IOException;\n";
		src += "import java.util.List;\n";
		src += "\n";
		src += "import lfocc.framework.compiler.ast.*;\n";
		src += "import lfocc.framework.compiler.ast.ASTVisitor.VisitorFailure;\n";
		src += "import lfocc.compilers." + cfg.name() + ".parser." + cfg.name() + "Parser;\n";
		src += "import lfocc.compilers." + cfg.name() + ".parser." + cfg.name() + "Parser.ParseException;\n";
		src += "import lfocc.compilers." + cfg.name() + ".parser." + cfg.name() + "Lexer;\n";
		src += "import lfocc.compilers." + cfg.name() + ".parser." + cfg.name() + "Lexer.ErrorReporter;\n";
		src += "import lfocc.compilers." + cfg.name() + ".options.Options;\n";
		if (backend != null)
			src += "import " + backendPackage + "." + backend + ";\n";
			src += "import lfocc.framework.compiler.Backend;\n";
			src += "import lfocc.framework.compiler.Backend.BackendFailure;\n";
		src += "\n";
		Iterator<String> semantics = semanticsPackages.values().iterator();
		while (semantics.hasNext()) {
			String _package = semantics.next();
			src += "import " + _package + ".*;\n";
		}
		src += "\n";
		src += "public class Application implements ErrorReporter {\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// Attributes
		///////////////////////////////////////////////////////////////////////
		src += "   private Options options;\n";
		src += "   private ASTNode root;\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// Constructor
		///////////////////////////////////////////////////////////////////////
		src += "   public Application(String[] args) {\n";
		src += "      options = new Options(args);\n";
		src += "   }\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// run()
		///////////////////////////////////////////////////////////////////////
		src += "   public void run() {\n";
		src += "      parse();\n";
		src += "      semantics();\n";
		src += "      transform();\n";
		src += "      generate();\n";
		src += "   }\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// parse()
		///////////////////////////////////////////////////////////////////////
		src += "   public void parse() {\n";
		src += "      try {\n";
		src += "         Reader in = new InputStreamReader(new FileInputStream(new File(options.getInput())));\n";
		src += "         " + cfg.name() + "Lexer lexer = new " + cfg.name() + "Lexer(in, this);\n";
		src += "         " + cfg.name() + "Parser parser = new " + cfg.name() + "Parser(this);\n";
		src += "         root = parser.parse(lexer);\n";
		src += "      } catch (FileNotFoundException e) {\n";
		src += "         System.out.println(String.format(\"File '%s' not found!\", options.getInput()));\n";
		src += "         System.exit(-1);\n";
		src += "      } catch (IOException e) {\n";
		src += "         System.out.println(String.format(\"IOException with file '%s'!\", options.getInput()));\n";
		src += "         System.exit(-1);\n";
		src += "      } catch (ParseException e) {\n";
		src += "         System.exit(-1);\n";
		src += "      }\n";
		src += "      \n";
		src += "   }\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// error(int line, String s)
		///////////////////////////////////////////////////////////////////////
		src += "   public void error(int line, String s) {\n";
		src += "      System.out.println(String.format(\"Parser Failure on line %d ('%s')!\", line, s));\n";
		src += "      System.exit(-1);\n";
		src += "   }\n";
		src += "   \n";
		
		///////////////////////////////////////////////////////////////////////
		// semantics()
		///////////////////////////////////////////////////////////////////////
		src += "   public void semantics() {\n";
		if (!semanticsNames.isEmpty()) {
			src += "      ASTVisitor visitor = null;\n";
			src += "      \n";
			src += "      try {\n";
			semantics = semanticsNames.values().iterator();
			while (semantics.hasNext()) {
				String transformer = semantics.next();
				src += "         \n";
				src += "         visitor = new " + transformer + "();\n";
				src += "         visitor.visit(root);\n";
				src += "         visitor.finish();\n";
			}
			src += "      \n";
			src += "      } catch (VisitorFailure f) {\n";
			src += "         System.out.println(\"Failure during semantic stage:\");\n";
			src += "         System.out.println(f.getMessage());\n";
			src += "         System.exit(-1);\n";
			src += "      }\n";
		} else {
			src += "      // No semantic transformations\n";
		}
		src += "   }\n";
		src += "   \n";

		///////////////////////////////////////////////////////////////////////
		// transform()
		///////////////////////////////////////////////////////////////////////
		src += "   public void transform() {\n";
		src += "      // TODO\n";
		src += "   }\n";
		src += "   \n";

		///////////////////////////////////////////////////////////////////////
		// generate()
		///////////////////////////////////////////////////////////////////////
		src += "   public void generate() {\n";
		src += "      \n";
		if (backend != null) {
			src += "      Backend backend = new " + backend + "();\n";
			src += "      try {\n";
			src += "         backend.generate(new File(options.getOutput()), root);\n";
			src += "      } catch (BackendFailure e) {\n";
			src += "         System.out.println(\"Failed to generate code!\");\n";
			src += "         System.out.println(e.getMessage());\n";
			src += "         System.exit(-1);\n";
			src += "      }\n";
		} else {
			src += "      // no backend provided, doing nothing\n";
		}
		src += "      \n";
		src += "   }\n";
		src += "   \n";

		src += "}\n";
		
		return src;
	}
	
	private void compileCompiler() {
		if (!JavaCompiler.compileFolder("lib/antlr-3.4.jar", binFolder, srcFolder)) {
			Logger.error("Failed to compile generated compiler!");
			exit(-1);
		}
		
	}
	
	public void run() {
		
		loadConfig();
		loadFeatures();
		setupFeatures();
		setupFeatureArrangements();
		setupOutputFolder();
		setupCompilerGenerator();
		Logger.info("Compiler successfully setup!");
		generateCompiler();
		Logger.info("Compiler successfully generated!");
		compileCompiler();
		Logger.info("Compiler successfully compiled!");
	}
	
	private void exit(int code) {
		System.exit(code);
	}

	////////////////////////////////////////////////////////////////////////////
	// CompilerGenerator methods:
	////////////////////////////////////////////////////////////////////////////
	
	@Override
	public ParserGenerator getParserGenerator() {
		return parserGenerator;
	}

	@Override
	public SemanticsGenerator getSemanticsGenerator() {
		return this;
	}
	
	@Override
	public BackendGenerator getBackendGenerator() {
		return this;
	}

	@Override
	public void addSource(String _package, File file) {
		sourceFiles.add(new SourceFile(_package, file));
	}
	public void addSource(String _package, String name, String source) {
		sourceCodes.add(new SourceCode(_package, name, source));
	}
	
	private static class SourceFile {

		private String _package;
		private File file;
		
		public SourceFile(String _package, File file) {
			this._package = _package;
			this.file = file;
		}

		public String getPackage() {
			return _package;
		}

		@SuppressWarnings("unused")
		public void setPackage(String _package) {
			this._package = _package;
		}

		public File getFile() {
			return file;
		}

		@SuppressWarnings("unused")
		public void setFile(File file) {
			this.file = file;
		}
	}
	
	private static class SourceCode {
		private String _package;
		private String code;
		private String name;
		
		public SourceCode(String _package, String name, String code) {
			this._package = _package;
			this.code = code;
			this.name = name;
		}
		
		public String getPackage() {
			return _package;
		}
		
		@SuppressWarnings("unused")
		public void setPackage(String _package) {
			this._package = _package;
		}
		
		public String getName() {
			return name;
		}
		
		@SuppressWarnings("unused")
		private void setName(String name) {
			this.name = name;
		}
		
		public String getCode() {
			return code;
		}
		
		@SuppressWarnings("unused")
		public void setCode(String code) {
			this.code = code;
		}
		
	}

	////////////////////////////////////////////////////////////////////////////
	// SemanticsGenerator methods:
	////////////////////////////////////////////////////////////////////////////

	@Override
	public void addTransformer(int priority, String packageName, String transformer) {
		assert(!semanticsNames.containsKey(priority));
		semanticsNames.put(priority, transformer);
		semanticsPackages.put(priority, packageName);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// SemanticsGenerator methods:
	////////////////////////////////////////////////////////////////////////////

	@Override
	public void setBackend(String _package, String backend) {
		assert this.backendPackage == null && this.backend == null;
		assert _package != null && backend != null;
		
		this.backendPackage = _package;
		this.backend = backend;
	}

	////////////////////////////////////////////////////////////////////////////
	// FeatureHelper methods:
	////////////////////////////////////////////////////////////////////////////

	@Override
	public File getConfiguration() {
		return configLoader.getConfigurationForFeature(currentFeature);
	}

	@Override
	public void printConfiguration(List<String> cfgs) {
		currentFeatureConfiguration = cfgs;
	}

	@Override
	public void depends(String dependency) {
		if (!features.keySet().contains(dependency)) {
			Logger.error(String.format("Unsatisfied dependencies in feature '%s'!", currentFeature));
			Logger.error(String.format("- %s", dependency));
			exit(-1);
		}
	}

	@Override
	public void depends(Set<String> dependencies) {
		if (dependencies != null && !features.keySet().containsAll(dependencies)) {
			Logger.error(String.format("Unsatisfied dependencies in feature '%s'!", currentFeature));
			dependencies.removeAll(features.keySet());
			Iterator<String> dependency = dependencies.iterator();
			while (dependency.hasNext()) {
				Logger.error(String.format("- %s", dependency.next()));
			}
			exit(-1);
		}
	}

	@Override
	public void registerService(Service service) {
		assert currentFeature.equals(service.getFeature());

		if (!services.containsKey(currentFeature))
			services.put(currentFeature, new HashMap<String, Service>());
		
		services.get(currentFeature).put(service.getServiceName(), service);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// ServiceProvider methods:
	////////////////////////////////////////////////////////////////////////////

	@Override
	public Service getService(String feature, String service) {
		if (services.containsKey(feature))
			if (services.get(feature).containsKey(service))
				return services.get(feature).get(service);
		return null;
	}

	////////////////////////////////////////////////////////////////////////////
	// FrameworkInterface methods:
	////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasFeature(String name) {
		return features.containsKey(name);
	}

	@Override
	public String getLanguageName() {
		return cfg.name();
	}

}
