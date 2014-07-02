package lfocc.framework.application;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.compilergenerator.parsergenerator.ParserGenerator;
import lfocc.framework.config.GlobalConfiguration;
import lfocc.framework.config.LanguageConfigurationLoader;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.FeatureLoader;
import lfocc.framework.feature.service.Service;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.util.Command;
import lfocc.framework.util.FileSystem;
import lfocc.framework.util.JavaCodeGen;
import lfocc.framework.util.JavaCompiler;
import lfocc.framework.util.Logger;

public class Application implements CompilerGenerator, FeatureHelper, ServiceProvider {
	
	private String[] args;
	private String compilerArgs = new String();
	private Map<String, Feature> features = new HashMap<String, Feature>();
	private LanguageConfigurationLoader configLoader = new LanguageConfigurationLoader();
	private GlobalConfiguration cfg;
	private Map<String, Map<String, Service>> services = new HashMap<String, Map<String, Service>>();
	private ParserGenerator parserGenerator = new ParserGenerator();
	private File outputFolder = null;
	private File srcFolder = null;
	private File parserFolder = null;
	private File binFolder = null;
	private String currentFeature = null; // relevant for the FeatureHelper interface
	private List<String> currentFeatureConfiguration = null;
	
	public Application(String[] args) {
		this.args = args;
		for (int i = 1; i < args.length; ++i) {
			compilerArgs += " " + args[i];
		}
	}

	public static void printUsage() {
		System.out.println("Usage: lfocc <langFile> [args]");
		System.out.println();
		System.out.println("      <langFile>      LanguageConfiguration file specifying the language.");
		System.out.println("      [args]          Arguments given to the created compiler.");
	}
	
	private void loadConfig() {

		if (args.length < 1) {
			Logger.error("No LanguageConfiguration file given!");
			printUsage();
			exit(-1);
		}

		configLoader.process(new File(args[0]));
		cfg = configLoader.getGlobalConfiguration();

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
			parserFolder = FileSystem.createFolder(srcFolder.getPath() + "/parser").toFile();
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
		if (!FileSystem.copyAll(
				FileSystem.getFiles("framework/lfocc/framework/compiler/"),
				srcFolder.getPath())) {
			
			Logger.error("Failed to copy compiler runtime to output folder!");
			exit(-1);
		}
		
		// generate files:
		try {
			FileSystem.writeTo(generateMainFile(), srcFolder.getPath() + "/Main.java");
			FileSystem.writeTo(generateApplicationFile(), srcFolder.getPath() + "/Application.java");
		} catch (IOException e1) {
			Logger.error("Failed to write main files!");
			e1.printStackTrace();
			exit(-1);
		}
		
		// parser generator:
		try {
			parserGenerator.copyTo(parserFolder, cfg.name());
		} catch (IOException e) {
			Logger.error("Failed to copy parser grammar to output folder!");
			e.printStackTrace();
			exit(-1);
		}
		if (!parserGenerator.generate(parserFolder)) {
			Logger.error("Failed to generate parser!");
			exit(-1);
		}
	}
	
	private String generateMainFile() {
		JavaCodeGen main = new JavaCodeGen();

		main.setPackage("lfocc.compilers." + cfg.name());
		main.emitLn();
		main.addImport("lfocc.compilers." + cfg.name() + ".application.Application");
		main.emitLn();

		main.startClass("public", "Main");
		main.startMethod("public static", "void", "main", "String[]", "args");
		main.emitLn("Application app = new Application(args);");
		main.emitLn("app.run();");
		main.endMethod();
		main.endClass();

		return main.generate();
	}
	
	private String generateApplicationFile() {
		JavaCodeGen app = new JavaCodeGen();
		
		app.setPackage(String.format("lfocc.compilers.%s.application", cfg.name()));
		app.emitLn();

		///////////////////////////////////////////////////////////////////////
		// Imports
		///////////////////////////////////////////////////////////////////////
		app.addImport(String.format("lfocc.compilers.%s.parser.RootParser", cfg.name()));
		app.addImport(String.format("lfocc.compilers.%s.parser.RootLexer", cfg.name()));
		app.emitLn();
		
		app.startClass("public", "Application");
		
		///////////////////////////////////////////////////////////////////////
		// Attributes
		///////////////////////////////////////////////////////////////////////
		app.emitLn("private String[] args;");
		app.emitLn();
		
		///////////////////////////////////////////////////////////////////////
		// Constructor
		///////////////////////////////////////////////////////////////////////
		app.startMethod("public", "", "Application", "String[]", "args");
		app.emitLn("this.args = args;");
		app.endMethod();
		app.emitLn();
		
		///////////////////////////////////////////////////////////////////////
		// run()
		///////////////////////////////////////////////////////////////////////
		app.startMethod("public", "void", "run");
		app.emitLn("parse();");
		app.emitLn("semantics();");
		app.emitLn("transform();");
		app.emitLn("generate();");
		app.endMethod();
		app.emitLn();
		
		///////////////////////////////////////////////////////////////////////
		// parse()
		///////////////////////////////////////////////////////////////////////
		app.startMethod("private", "void", "parse");
		app.emitLn("// TODO");
		app.endMethod();
		app.emitLn();
		
		///////////////////////////////////////////////////////////////////////
		// semantics()
		///////////////////////////////////////////////////////////////////////
		app.startMethod("private", "void", "semantics");
		app.emitLn("// TODO");
		app.endMethod();
		app.emitLn();

		///////////////////////////////////////////////////////////////////////
		// transform()
		///////////////////////////////////////////////////////////////////////
		app.startMethod("private", "void", "transform");
		app.emitLn("// TODO");
		app.endMethod();
		app.emitLn();

		///////////////////////////////////////////////////////////////////////
		// generate()
		///////////////////////////////////////////////////////////////////////
		app.startMethod("private", "void", "generate");
		app.emitLn("// TODO");
		app.endMethod();
		app.emitLn();

		app.endClass();
		
		return app.generate();
	}
	
	private void compileCompiler() {
		if (!JavaCompiler.compileFolder("lib/antlr-3.4.jar", binFolder, srcFolder)) {
			Logger.error("Failed to compile generated compiler!");
			exit(-1);
		}
		
	}
	
	private void startCompiler() {

		if (compilerArgs.length() != 0) {
			Logger.info("Starting compiler now with:");
			Logger.info("<lfocc> " + compilerArgs);
		} else {
			Logger.info("Starting compiler now without arguments:");
		}

		String command = "java" + 
				" -cp lib/antlr-3.4.jar:" + binFolder.getPath() + 
				" lfocc.compilers." + cfg.name() + ".Main" +
				" " + compilerArgs;

		// TODO: set current directory to compiler's directory for compiler
		if (!Command.execute(command, true)) {
			Logger.error("Failed to execute compiler!");
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
		startCompiler();
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
}
