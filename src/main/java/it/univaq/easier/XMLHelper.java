package it.univaq.easier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper class to parse XML files.
 */
public class XMLHelper {
	
	private static final Logger LOGGER = Logger.getLogger(XMLHelper.class.getName());
	
	/**
	 * Parse an XML file and return a org.w3c.dom.Document.
	 * 
	 * @param filename Path to the XML file
	 * @return Document
	 */
	public static Document parseXML(final String filename) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(new File(filename));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			LOGGER.severe("Cannot parse: " + filename);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Given an XML element, it will find all the elements with the provided tag name.
	 * It takes care of casting them to Element.
	 * 
	 * @param root Starting element for the search
	 * @param tag Name of the tag to search
	 * @return List of Elements
	 */
	public static List<Element> getElementsByTagName(final Element root, final String tag) {
		final NodeList elements = root.getElementsByTagName(tag);
		final List<Element> list = new ArrayList<>();
		for (int i = 0; i < elements.getLength(); i++) {
			list.add((Element) elements.item(i));
		}
		return list;
	}
}
