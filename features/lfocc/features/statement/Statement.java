package lfocc.features.statement;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class Statement extends SyntaxExtendable {

	// TODO: add configurability
	private boolean assignment = true; // whether to active assingment statements
	private boolean codeblock = true; // whether statements can occur in code blocks
	
	@Override
	public void setup(FeatureHelper helper) {
		if (assignment) {
			helper.depends("Expressions");
			helper.depends("Variables");
		}
		
		if (codeblock)
			helper.depends("CodeBlock");
		
		helper.registerService(new SyntaxExtender(this));
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		if (codeblock) {
			SyntaxExtender extender = (SyntaxExtender) services.getService("CodeBlock", "SyntaxExtender");
			extender.addSyntaxRule("statement ';'");
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateInternalGrammar());
	}
	
	public String generateInternalGrammar() {
		String src = "";
		src += "statement : ( internalStatement | externalStatement ) ;\n";
		src += "\n";
		src += "\n";
		src += "internalStatement :\n";

		if (assignment)
			// TODO: assignment target
			src += "   '=' expression\n";

		src += "   ;\n";
		src += "\n";
		src += "externalStatement :\n";
		
		Iterator<String> it = rules.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";

			while (it.hasNext())
				src += "   | " + it.next() + "\n";

			src += "   )\n";
		}
		src += "   ;\n";
		
		return src;
	}
}
