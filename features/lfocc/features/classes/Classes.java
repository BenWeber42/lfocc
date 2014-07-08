package lfocc.features.classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.MultiExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.ExtenderService;

public class Classes extends MultiExtendable {
	
	// TODO: new operator
	
	// TODO: add configurability
	private boolean inheritance = true;
	
	private static final String classBodyExtender = "BodyExtender";
	private static final String objectProviderExtender = "ObjectProvider";
	private static final String objectMemberExtender = "ObjectMember";
	
	private boolean expressions;

	public Classes() {
		super(new HashSet<String>(Arrays.asList(
				classBodyExtender, objectProviderExtender, objectMemberExtender)));
	}

	public Set<String> getDependencies() {
		return new HashSet<String>(Arrays.asList(
				"GlobalScope",
				"SyntaxBase"));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends(getDependencies());
		helper.registerService(getExtender(classBodyExtender));
		helper.registerService(getExtender(objectProviderExtender));
		helper.registerService(getExtender(objectMemberExtender));
		expressions = helper.hasFeature("Expressions");
	}


	@Override
	public void setupFeatureArrangements(ServiceProvider serviceManager) {
		ExtenderService extender = (ExtenderService)
				serviceManager.getService("GlobalScope", "Extender");
		extender.addSyntaxRule("classDecl");
		
		if (expressions && !getExtensions(objectProviderExtender).isEmpty()) {
			extender = (ExtenderService)
					serviceManager.getService("Expressions", "Extender");
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
