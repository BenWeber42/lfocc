package lfocc.features.classes;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.w3c.dom.Document;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.MultiExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.util.XML;

public class Classes extends MultiExtendable {
	
	// TODO: new operator
	
	public static final String CLASSES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/classes/ConfigSchema.xsd";
	
	private boolean inheritance = true;
	
	private static final String classBodyExtender = "BodyExtender";
	private static final String objectProviderExtender = "ObjectProvider";
	private static final String objectMemberExtender = "ObjectMember";
	
	public Classes() {
		super(new HashSet<String>(Arrays.asList(
				classBodyExtender, objectProviderExtender, objectMemberExtender)));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("GlobalScope");
		helper.depends("SyntaxBase");
		helper.registerService(getExtender(classBodyExtender));
		helper.registerService(getExtender(objectProviderExtender));
		helper.registerService(getExtender(objectMemberExtender));
		
		if (helper.getConfiguration() != null) {
			Document config = XML.load(helper.getConfiguration(),
					new File(CLASSES_CONFIGURATION_SCHEMA));
			inheritance = XML.getBooleanOption(config, "Inheritance");
		}
		helper.printConfiguration(Arrays.asList("Inheritance = " + inheritance));
	}


	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		ExtenderService extender = (ExtenderService)
				services.getService("GlobalScope", "Extender");
		extender.addSyntaxRule("classDecl");
		
		if (services.hasFeature("Expressions") && !getExtensions(objectProviderExtender).isEmpty()) {
			extender = (ExtenderService)
					services.getService("Expressions", "Extender");
			extender.addSyntaxRule(generateExpressionRule());
		}
	}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
		
	}
	
	private String generateParserSource() {
		String src = "";
		if (inheritance)
			src += "classDecl : 'class' Identifier ( 'extends' Identifier )? '{' \n";
		else
			src += "classDecl : 'class' Identifier '{' \n";
		
		Iterator<String> it = getExtensions(classBodyExtender).iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "      " + it.next() + "\n";

			while (it.hasNext())
				src += "      | " + it.next() + "\n";

			src += "   )*\n";
		}
		
		src += "   '}' ;\n";	
		return src;
	}
	
	private String generateExpressionRule() {

		String rule = "";

		Iterator<String> it = getExtensions(objectProviderExtender).iterator();
		if (it.hasNext()) {
			rule += "( ";
			rule += it.next();
			while (it.hasNext())
				rule += " | " + it.next();
			rule += " )";
		}
		
		it = getExtensions(objectProviderExtender).iterator();
		if (it.hasNext()) {
			rule += " ( '.' (";
			rule += it.next();
			while (it.hasNext())
				rule += " | " + it.next();
			rule += ") )*";
		}
			
		return rule;
	}
}
