package lfocc.features.x86.backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import lfocc.framework.compiler.Backend;
import lfocc.framework.compiler.ast.ASTNode;
import lfocc.framework.util.Command;
import lfocc.framework.util.Command.CommandOutput;
import lfocc.framework.util.StringUtil;

public class X86Backend implements Backend {
//
//	@Override
	public void generate(File output, ASTNode root) throws BackendFailure {
		
		// TODO: parametrize assembly file suffix
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
		// simple hello world program to test correct gcc call
		String src = "";
        src += "        .section        .rodata\n";
        src += ".LC0:\n";
        src += "        .string \"Hello, World!\"\n";
        src += "        .text\n";
        src += "        .globl  main\n";
        src += "        .type   main, @function\n";
        src += "main:\n";
        src += ".LFB0:\n";
        src += "        .cfi_startproc\n";
        src += "        leal    4(%esp), %ecx\n";
        src += "        .cfi_def_cfa 1, 0\n";
        src += "        andl    $-16, %esp\n";
        src += "        pushl   -4(%ecx)\n";
        src += "        pushl   %ebp\n";
        src += "        .cfi_escape 0x10,0x5,0x2,0x75,0\n";
        src += "        movl    %esp, %ebp\n";
        src += "        pushl   %ecx\n";
        src += "        .cfi_escape 0xf,0x3,0x75,0x7c,0x6\n";
        src += "        subl    $4, %esp\n";
        src += "        subl    $12, %esp\n";
        src += "        pushl   $.LC0\n";
        src += "        call    puts\n";
        src += "        addl    $16, %esp\n";
        src += "        movl    $0, %eax\n";
        src += "        movl    -4(%ebp), %ecx\n";
        src += "        .cfi_def_cfa 1, 0\n";
        src += "        leave\n";
        src += "        .cfi_restore 5\n";
        src += "        leal    -4(%ecx), %esp\n";
        src += "        .cfi_def_cfa 4, 4\n";
        src += "        ret\n";
        src += "        .cfi_endproc\n";
        src += ".LFE0:\n";
        src += "        .size   main, .-main\n";
        src += "        .ident  \"GCC: (Ubuntu 4.9.2-10ubuntu13) 4.9.2\"\n";
        src += "        .section        .note.GNU-stack,\"\",@progbits\n";

        return src;
	}

}
