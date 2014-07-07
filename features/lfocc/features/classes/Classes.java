package lfocc.features.classes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SyntaxExtendable;
import lfocc.framework.feature.service.ServiceProvider;
import lfocc.framework.feature.service.SyntaxExtender;

public class Classes extends SyntaxExtendable {
	
	// TODO: add configurability
	private boolean inheritance = true;

	public Set<String> getDependencies() {
		return new HashSet<String>(Arrays.asList(
				"GlobalScope",
				"SyntaxBase"));
	}

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends(getDependencies());
		helper.registerService(new SyntaxExtender(this));
	}


	@Override
	public void setupFeatureArrangements(ServiceProvider serviceManager) {
		SyntaxExtender globalScope = (SyntaxExtender)
				serviceManager.getService("GlobalScope", "SyntaxExtender");
		globalScope.addSyntaxRule("classDecl");

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
		
		Iterator<String> it = rules.iterator();
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
}
