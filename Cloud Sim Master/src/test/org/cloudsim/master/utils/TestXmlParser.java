package test.org.cloudsim.master.utils;

import static junit.framework.Assert.assertNotNull;
import static main.org.cloudsim.master.utils.XmlParser.createDocument;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import main.org.cloudsim.master.utils.Constants;
import main.org.cloudsim.master.utils.XmlParser;

import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestXmlParser {

	@Test
	public void shouldCreateDocument() throws ParserConfigurationException,
			SAXException, IOException {
		String filePath = "D:\\Proiecte-acasa\\Cloud Sim Master\\configurations.xml";
		Document document = createDocument(filePath);
		assertNotNull(document);
	}
	
	@Test
	public void shouldRetrieveIntegerValue() throws ParserConfigurationException, SAXException, IOException
	{
		String filePath = "D:\\Proiecte-acasa\\Cloud Sim Master\\configurations.xml";
		Document document = createDocument(filePath);
		
		int actual = XmlParser.getIntegerValue(document, Constants.MAX_NUMBER_HOST);
		assertNotNull(actual);
	}
}
