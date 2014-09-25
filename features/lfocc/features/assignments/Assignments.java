package lfocc.features.assignments;

import java.io.File;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.Feature;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.service.ExtenderService;
import lfocc.framework.feature.service.ServiceProvider;

public class Assignments extends Feature {

	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("Expressions");
		helper.depends("Statement");
	}
	
	@Override
	public void setupFeatureArrangements(ServiceProvider services) {
		ExtenderService extender = (ExtenderService) services.getService("Statement", "Extender");
		extender.addSyntaxRule(
				"assignment" +
				"   {\n" +
				"      $$ = new ArrayList<ASTNode>(Arrays.asList($assignment));\n" +
				"   }\n"
				);
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addToken("'='", "/=/");

		cg.getParserGenerator().addImport("lfocc.features.assignments.ast.*");
		
		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());

		cg.addSource("lfocc.features.statement.ast",
				new File("features/lfocc/features/assignments/ast/Assignment.java"));
		cg.addSource("lfocc.features.statement.semantics",
				new File("features/lfocc/features/assignments/semantics/AssignmentFailure.java"));
	}
	
	private String generateGrammar() {
		String src = "";
		src += "assignment (Assignment) ::=\n";
		src += "   assignableExpression '=' expression\n";
		src += "   {\n";
		src += "      $$ = new Assignment($assignableExpression, $expression);\n";
		src += "   }\n";
		src += "   ;\n";
		
		return src;

	}
}
