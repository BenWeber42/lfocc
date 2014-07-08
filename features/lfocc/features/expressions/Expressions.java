package lfocc.features.expressions;

import java.util.Iterator;

import lfocc.framework.compilergenerator.CompilerGenerator;
import lfocc.framework.feature.FeatureHelper;
import lfocc.framework.feature.SingleExtendable;

public class Expressions extends SingleExtendable {
	
	@Override
	public void setup(FeatureHelper helper) {
		helper.registerService(getExtender());
	}
	
	@Override
	public void setupCompilerGenerator(CompilerGenerator cg) {
		cg.getParserGenerator().addParserSource(getName(), generateExternalSource());
		cg.getParserGenerator().addParserSource(getName(), generateInternalSource());
	}
	
	private String generateExternalSource() {
		String src = "";
		src += "externalExpression :\n";
		
		Iterator<String> it = extensions.iterator();
		if (it.hasNext()) {
			src += "   (\n";
			src += "   " + it.next() + "\n";
			while (it.hasNext()) {
				src += "   | " + it.next() + "\n";
			}
			src += "   )\n";
		}
		src += "   ;\n";
		
		return src;
	}
	
	private String generateInternalSource() {
		String src = "";
		
		src += "expression :\n";
		src += "   comparison\n";
		src += "   ;\n";
		src += "\n";
		src += "comparison :\n";
		src += "   addition (\n";
		src += "   ( '==' | '!=' | '<' | '<=' | '>' | '>='	)\n";
		src += "   addition )?\n";
		src += "   ;\n";
		src += "\n";
		src += "addition :\n";
		src += "   multiplication (\n";
		src += "   ( '+' | '-' | '||' )\n";
		src += "   multiplication )?\n";
		src += "   ;\n";
		src += "\n";
		src += "multiplication :\n";
		src += "   unary (\n";
		src += "   ( '*' | '/' | '%' | '&&' )\n";
		src += "   unary )?\n";
		src += "   ;\n";
		src += "\n";
		src += "unary :\n";
		src += "   ( '+' | '-' )? leafExpression\n";
		src += "   ;\n";
		src += "\n";
		src += "leafExpression :\n";
		src += "   (\n";
		src += "   '(' expression ')'\n";
		src += "   | Integer\n";
		src += "   | Float\n";
		src += "   | Boolean\n";
		src += "   | externalExpression\n";
		src += "   )\n";
		src += "   ;\n";
		src += "\n";
		src += "Integer : (\n";
		src += "   ('1'..'9') ('0'..'9')*\n";
		src += "   | '0x' ('0'..'9' | 'a'..'f' | 'A'..'F' )*\n";
		src += "   );\n";
		src += "\n";
		src += "Float : ('1'..'9') ('0'..'9')* '.' ('0'..'9')*;\n";
		src += "\n";
		src += "Boolean : ( 'true' | 'false' ) ;\n";
		return src;
	}
}
