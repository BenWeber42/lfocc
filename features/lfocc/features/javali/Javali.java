package lfocc.features.javali;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FrameworkInterface;

/**
 * The Javali back-end
 */
public class Javali extends Feature {
	
	private static final String name = "JavaliBackend";
	private String _package;
	
	public void setupCompilerGenerator(CompilerGenerator cg) {
		
		_package = "lfocc.compilers." + cg.getLanguageName() + ".backend";
		cg.addSource(_package, name, generateBackend(cg));
		cg.getBackendGenerator().setBackend(_package, name);
	}
	
	private String generateBackend(FrameworkInterface framework) {
		String src = "";
		
		src += "package " + _package + ";\n";
		src += "\n";
		src += "import lfocc.framework.compiler.Backend;\n";
		src += "import lfocc.framework.compiler.ast.ASTNode;\n";
		src += "\n";
		src += "public class " + name + " implements Backend {\n";
		src += "\n";
		src += "   @Override\n";
		src += "   public void generate(StringBuilder out, ASTNode root) {\n";
		src += "      // TODO\n";
		src += "      out.append(\"Hello, World!\");\n";
		src += "   }\n";
		src += "\n";
		src += "}\n";
		
		return src;
	}

}
