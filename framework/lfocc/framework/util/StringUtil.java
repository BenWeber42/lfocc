package lfocc.framework.util;

import java.util.Iterator;
import java.util.List;

public class StringUtil {
	
	public static String join(List<String> strings) {
		return join("", strings);
	}

	public static String join(String separator, List<String> strings) {
		String str = "";

		Iterator<String> it = strings.iterator();
		
		if (it.hasNext()) {
			str += it.next();
			
			while (it.hasNext())
				str += separator + it.next();
		}
		
		return str;
	}
}
