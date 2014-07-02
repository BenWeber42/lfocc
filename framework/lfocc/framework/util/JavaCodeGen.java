package lfocc.framework.util;

public class JavaCodeGen {

	// TODO: Add semantic checks

	private StringBuilder source = new StringBuilder();
	private int indent = 0;
	
	public void emit(String src) {
		source.append(src);
	}
	
	public void emitLn() {
		emit("\n");
	}
	
	public void emitIndentation() {
		for (int i = 0; i < indent; ++i) {
			emit("   ");
		}
	}
	
	public void emit(String src, Object... args) {
		emit(String.format(src, args));
	}
	
	public void emitLn(String src) {
		emitIndentation();
		emit(src);
		emitLn();
	}
	
	public void emitLn(String src, Object... args) {
		emitIndentation();
		emit(src, args);
		emitLn();
	}
	
	public void setPackage(String name) {
		emitLn("package %s;", name);
	}
	
	public void addImport(String _import) {
		emitLn("import %s;", _import);
	}
	
	public void startClass(String modifiers, String name) {
		emitIndentation();
		if (modifiers != null && !modifiers.equals(""))
			emitLn("%s class %s {", modifiers, name);
		else
			emitLn("class %s {", name);
		++indent;
	}
	
	public void endClass() {
		--indent;
		emitLn("}");
	}
	
	public void startMethod(String modifiers, String returnType, String name, String... param) {
		assert param.length % 2 == 0;
		
		emitIndentation();
		emit("%s %s %s(", modifiers, returnType, name);

		if (param.length > 0) {
			emit("%s %s", param[0], param[1]);
			for (int i = 2; i < param.length; i += 2) {
				emit(", %s %s", param[i], param[i + 1]);
			}
		}
		
		emit(") {");
		emit("\n");
		++indent;
	}
	
	public void endMethod() {
		--indent;
		emitLn("}");
	}
	
	public String generate() {
		return source.toString();
	}
}
