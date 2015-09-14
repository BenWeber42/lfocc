package lfocc.features.x86.backend;

public class LabelManager {
	private static final String LABEL_ESCAPE = "label__";
	private int counter = 0;
	
	public String generateLabel() {
		++counter;
		return CodeGeneratorHelper.escape(LABEL_ESCAPE + counter);
	}
}
