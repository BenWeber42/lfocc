package lfocc.framework.util;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XML {
	
	public static Document load(File xml, File schema) {
		DocumentBuilderFactory docFactory = 
				DocumentBuilderFactory.newInstance();

		try {
			docFactory.setSchema(
					SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
					.newSchema(schema));
		} catch (SAXException e) {
			Logger.error("Failed to load LanguageConfiguration XMLScheme File!");
			Logger.info(String.format("It should be located at '%s'.", schema.toString()));
			e.printStackTrace();
		}

		Document doc = null;
		try {
			doc = docFactory.newDocumentBuilder().parse(xml);
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return doc;
	}
	
	public static Document load(File xml) {
		return load(xml, null);
	}
	
	public static boolean getBooleanOption(Document doc, String name) {
		return doc.getElementsByTagName(name).item(0).getTextContent().equals("true");
	}
	
	public static String getStringOption(Document doc, String name) {
		return doc.getElementsByTagName(name).item(0).getTextContent();
	}

}
