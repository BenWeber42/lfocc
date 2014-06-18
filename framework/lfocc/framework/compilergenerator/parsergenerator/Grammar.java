package lfocc.framework.compilergenerator.parsergenerator;

import java.io.File;
import java.io.IOException;

public abstract class Grammar {
	
	protected String name;
	protected String feature;
	public abstract void copyTo(File destination) throws IOException;

	public String getName() {
		return name;
	}
	
	public String getFeature() {
		return feature;
	}
}
