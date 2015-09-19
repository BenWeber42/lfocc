package lfocc.features.x86.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import lfocc.features.globalscope.ast.GlobalScope;
import lfocc.framework.compiler.Backend;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.util.Command;
import lfocc.framework.util.Command.CommandOutput;
import lfocc.framework.util.StringUtil;

public class X86Backend implements Backend {

	@Override
	public void generate(File output, ASTNode root) throws BackendFailure {
		
		// LATER: parametrize assembly file suffix
		File assembler = new File(output + ".s");
		try {
			Writer writer = new BufferedWriter(new FileWriter(assembler));
			writer.write(generateAssembler(root));
			writer.close();
		} catch (IOException e) {
			throw new BackendFailure("Failed to write to assembly output file '" + assembler + "'!");
		}
		
		CommandOutput compilationOutput = Command.executeWithOutput(
				new String[] {"gcc", "-m32", "-o", output.getAbsolutePath(), assembler.getAbsolutePath()});
		
		if (!compilationOutput.success()) {
			throw new BackendFailure("Failed to compile assembly!\n"
					+ StringUtil.join("\n", compilationOutput.output()));
		}
		
	}
	
	private String generateAssembler(ASTNode root) throws BackendFailure {
		assert root instanceof GlobalScope;
		return new CodeGenerator().generate((GlobalScope) root);
	}

}
