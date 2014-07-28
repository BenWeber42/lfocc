package lfocc.features.classes;

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

public class Classes extends SingleExtendable {
	
	// TODO: new operator
	
	public static final String CLASSES_CONFIGURATION_SCHEMA =
			"features/lfocc/features/classes/ConfigSchema.xsd";
	
	private boolean inheritance = true;
	
	private static final String objectMemberExtender = "ObjectMember";
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.depends("GlobalScope");
		helper.depends("SyntaxBase");
		helper.registerService(getExtender());
		
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
		
		if (services.hasFeature("Expressions")) {
			extender = (ExtenderService)
					services.getService("Expressions", "ExpressionExtender");

			extender.addSyntaxRule(
					"'new' identifier '(' ')'" +
					"   {\n" +
					"      $$ = new NewOperator($identifier);\n" +
					"   }\n"
					);

			/*
			 * Due to limitations of the LALR(1) parser and to keep it simple
			 * we'll use a special syntax for casts.
			 */
			
			extender.addSyntaxRule(
					"'cast' '<' identifier '>' expression\n" +
				    "   {\n" +
				    "      $$ = new CastExpression($identifier, $expression#1);\n" +
				    "   }\n"
					);
			
			extender.addSyntaxRule(
					"'null'\n" +
				    "   {\n" +
				    "      $$ = new NullExpression();\n" +
				    "   }\n"
					);
			
		}
	}

	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addToken("'class'", "/class/");

		if (inheritance)
			cg.getParserGenerator().addToken("'extends'", "/extends/");

		if (!objectMemberExtender.isEmpty()) {
			cg.getParserGenerator().addToken("'.'", "/\\./");
			cg.getParserGenerator().addPrecedence("'.'", 4);
		}

		if (cg.hasFeature("Expressions")) {
			cg.getParserGenerator().addToken("'new'", "/new/");
			// expressions will register '<' and '>'
			cg.getParserGenerator().addToken("'cast'", "/cast/");
			cg.getParserGenerator().addToken("'null'", "/null/");
		}

		cg.getParserGenerator().addParserSource(getName(), generateParserSource());
		
		cg.getParserGenerator().addImport("lfocc.features.classes.ast.*");

		cg.addSource("lfocc.features.classes.ast", new File("features/lfocc/features/classes/ast/CastExpression.java"));
		cg.addSource("lfocc.features.classes.ast", new File("features/lfocc/features/classes/ast/NewOperator.java"));
		cg.addSource("lfocc.features.classes.ast", new File("features/lfocc/features/classes/ast/NullExpression.java"));
		
	}
	
	private String generateParserSource() {
		String src = "";
		src += "classDecl ::= 'class' identifier '{' classBody '}' ;\n";

		if (inheritance)
			src += "classDecl ::= 'class' identifier 'extends' identifier '{' classBody '}' ;\n";
		
		src += "\n";
		src += "classBody ::=\n";
		src += "   # empty\n";
		src += "   | _classBody\n";
		src += "   ;\n";
		src += "\n";
		src += "_classBody ::=\n";
		src += "   classBodyElement\n";
		src += "   | classBodyElement _classBody\n";
		src += "   ;\n";
		src += "\n";
		src += "classBodyElement ::=\n";

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
