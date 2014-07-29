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
		extender.addSyntaxRule(
				"classDecl\n" +
				"   {\n" +
				"      $$ = new ArrayList<ASTNode>(Arrays.asList($classDecl));\n" +
				"   }\n"
				);
		
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
					"'cast' '<' identifier '>' arg = expression\n" +
				    "   {\n" +
				    "      $$ = new CastExpression($identifier, $arg);\n" +
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

		cg.getParserGenerator().addGrammarSource(getName(), generateGrammar());
		
		cg.getParserGenerator().addImport("lfocc.features.classes.ast.*");

		cg.addSource("lfocc.features.classes.ast",
				new File("features/lfocc/features/classes/ast/CastExpression.java"));
		cg.addSource("lfocc.features.classes.ast",
				new File("features/lfocc/features/classes/ast/NewOperator.java"));
		cg.addSource("lfocc.features.classes.ast",
				new File("features/lfocc/features/classes/ast/NullExpression.java"));
		cg.addSource("lfocc.features.classes.ast",
				new File("features/lfocc/features/classes/ast/ClassNode.java"));
		
	}
	
	private String generateGrammar() {
		String src = "";
		src += "classDecl (ClassNode) ::= \n";
		src += "   'class' name = identifier '{' classBody '}'\n";
		src += "   {\n";
		src += "      $$ = new ClassNode($name, null, $classBody);\n";
		src += "   }\n";
		src += "   \n";
		if (inheritance) {
			src += "   | 'class' name = identifier 'extends' parent = identifier '{' classBody '}'\n";
			src += "   {\n";
			src += "      $$ = new ClassNode($name, $parent, $classBody);\n";
			src += "   }\n";
			src += "   \n";
		}
		src += "   ;\n";
		src += "\n";
		src += "classBody (List<ASTNode>) ::=\n";
		src += "   # empty\n";
		src += "   {\n";
		src += "      $$ = new ArrayList<ASTNode>();\n";
		src += "   }\n";
		src += "   \n";
		src += "   | members = _classBody\n";
		src += "   {\n";
		src += "      $$ = $members;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "_classBody (List<ASTNode>) ::=\n";
		src += "   classBodyElement\n";
		src += "   {\n";
		src += "      $$ = $classBodyElement;\n";
		src += "   }\n";
		src += "   \n";
		src += "   | prev = classBodyElement next = _classBody\n";
		src += "   {\n";
		src += "      $prev.addAll($next);\n";
		src += "      $$ = $prev;\n";
		src += "   }\n";
		src += "   \n";
		src += "   ;\n";
		src += "\n";
		src += "classBodyElement (List<ASTNode>) ::=\n";

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
