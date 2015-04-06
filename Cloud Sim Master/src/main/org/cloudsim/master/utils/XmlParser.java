package main.org.cloudsim.master.utils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public final class XmlParser {

	private XmlParser() {

	}

	public static Document createDocument(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	public static int getIntegerValue(Document document, String nodeName)
	{
		Node node = document.getElementsByTagName(nodeName).item(0);
		return Integer.parseInt(node.getTextContent());
	}
	
	public static String getStringValue(Document document, String nodeName)
	{
		Node node = document.getElementsByTagName(nodeName).item(0);
		return node.getTextContent();
	}
	
	public static BigDecimal getBigDecimalValue(Document document, String nodeName)
	{
		Node node = document.getElementsByTagName(nodeName).item(0);
		return new BigDecimal(node.getTextContent());
	}
}
