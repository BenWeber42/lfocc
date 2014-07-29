package lfocc.features.statement;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Statement extends SingleExtendable {
	
	public static final String STATEMENT_CONFIGURATION_SCHEMA =
			"features/lfocc/features/statement/ConfigSchema.xsd";

	private boolean codeblock = true; // whether statements can occur in code blocks
	
	@Override
	public void setup(FeatureHelper helper) {

		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(STATEMENT_CONFIGURATION_SCHEMA));
			codeblock = XML.getBooleanOption(cfg, "Codeblock");
		}
		
		helper.printConfiguration(Arrays.asList(
				"Codeblock = " + codeblock));

		if (codeblock)
			helper.depends("CodeBlock");
		
		helper.registerService(getExtender());
		
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		if (codeblock) {
			ExtenderService extender = (ExtenderService) services.getService("CodeBlock", "Extender");
			extender.addSyntaxRule(
					"statement ';'\n" +
					"   {\n" +
					"      $$ = $statement;\n" +
					"   }\n"
					);
		}
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());
	}
	
	private String generateGrammar() {
		String src = "";
		src += "statement (List<ASTNode>)::= \n";

		Iterator<String> it = extensions.iterator();

		if (it.hasNext()) {
			src += "   " + it.next() + "\n";
		
			while (it.hasNext())
				src += "   | " + it.next() + "\n";
		}

		src += "   ;\n";
		
		return src;
	}
}
