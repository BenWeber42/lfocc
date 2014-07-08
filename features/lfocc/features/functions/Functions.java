package lfocc.features.functions;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.MultiExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Functions extends MultiExtendable {
	
	public static final String FUNCTIONS_CONFIGURATION_SCHEMA = 
			"features/lfocc/features/functions/ConfigSchema.xsd";

	private boolean globals = true; // function declarations in global scope
	private boolean classMembers = true; // function declarations as class members
	private boolean returnValue = true; // return value allowed

	private static final String declarationExtender = "DeclarationExtender";
	private static final String callExtender = "CallExtender";
	
	public Functions() {
		super(new HashSet<String>(Arrays.asList(callExtender, declarationExtender)));
	}

	public void setup(FeatureHelper helper) {

		if (helper.getConfiguration() != null) {
			Document cfg = XML.load(helper.getConfiguration(),
					new File(FUNCTIONS_CONFIGURATION_SCHEMA));
			globals = XML.getBooleanOption(cfg, "Globals");
			classMembers = XML.getBooleanOption(cfg, "ClassMembers");
			returnValue = XML.getBooleanOption(cfg, "ReturnValue");
		}
		
		helper.printConfiguration(Arrays.asList(
				"Globals = " + globals,
				"ClassMembers = " + classMembers,
				"ReturnValue = " + returnValue));
		
		if (!globals && !classMembers)
			return; // function declarations can't occur anywhere, nothing to do

		helper.depends("CodeBlock");
		
		if (globals)
			helper.depends("GlobalScope");
		if (classMembers)
			helper.depends("Classes");
		if (returnValue)
			helper.depends("Expressions");
		
		helper.registerService(getExtender(callExtender));
		helper.registerService(getExtender(declarationExtender));
		
	}
	
	public void setupFeatureArrangements(ServiceProvider services) {
		
		if (!globals && !classMembers)
			return;

		ExtenderService extender = (ExtenderService) 
				services.getService("CodeBlock", "Extender");
		extender.addSyntaxRule("returnStmt");
		
		if (globals) {
			extender = (ExtenderService) 
					services.getService("GlobalScope", "Extender");
			extender.addSyntaxRule("functionDeclaration");
		}

		if (services.hasFeature("Statement")) {
			extender = (ExtenderService) 
					services.getService("Statement", "Extender");
			extender.addSyntaxRule("functionCall");
		}

		/*
		 * The Classes featuer will register all ObjectProviders as expressions.
		 * So if the Classes feature is activeted we don't have to register variable
		 * usage as expression.
		 * 
		 * If the Classes feature is deactivated and the Expressions feature is
		 * activated, we have to register variable usage as expression ourself.
		 */

		if (!services.hasFeature("Classes") && services.hasFeature("Expressions")) {
			extender = (ExtenderService) 
					services.getService("Expressions", "Extender");
			extender.addSyntaxRule("functionCall");
		}

		if (services.hasFeature("Classes")) {
			extender = (ExtenderService) 
					services.getService("Classes", "ObjectProvider");
			extender.addSyntaxRule("functionCall");
		}

		if (classMembers) {
			extender = (ExtenderService) 
					services.getService("Classes", "BodyExtender");
			extender.addSyntaxRule("functionDeclaration");
			extender = (ExtenderService) 
					services.getService("Classes", "ObjectMember");
			extender.addSyntaxRule("functionCall");
		}

	}
	
	public void setupCompilerGenerator(CompilerGenerator cg) {

		if (!globals && !classMembers)
			return;

		cg.getParserGenerator().addParserSource(getName(), generateFunctionGrammar());
		cg.getParserGenerator().addParserSource(getName(), generateReturnGrammar());
	}
	
	public String generateFunctionGrammar() {
		String src = "";
		src += "functionDeclaration :\n";
		src += "   Identifier Identifier";
		src += "   '('\n";
		
		Iterator<String> it = getExtensions(declarationExtender).iterator();
		if (it.hasNext()) {
			src += "      (\n";
			src += "      " + it.next() + "\n";
			
			while (it.hasNext())
				src += "      | " + it.next() + "\n";

			src += "      )?\n";
		}
		
		src += "   ')'\n";
		src += "   '{' codeBlock '}'\n";
		src += "   ;\n";
		src += "\n";
		src += "functionCall :\n";
		src += "   Identifier\n";
		src += "   '('\n";

		it = getExtensions(callExtender).iterator();
		if (it.hasNext()) {
			src += "      (\n";
			src += "      " + it.next() + "\n";
			
			while (it.hasNext())
				src += "      | " + it.next() + "\n";

			src += "      )?\n";
		}
	
		src += "   ')'\n";
		src += "   ;\n";
		
		return src;
	}
	
	public String generateReturnGrammar() {
		String src = "";
		if (returnValue)
			src += "returnStmt : 'return' ( expression )? ';' ;\n";
		else
			src += "returnStmt : 'return' ';' ;\n";
		
		return src;
	}
}
