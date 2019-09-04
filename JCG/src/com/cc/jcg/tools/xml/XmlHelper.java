package com.cc.jcg.tools.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class XmlHelper {

    private XmlHelper() {
	super();
    }

    public static final Document newDocument() throws ParserConfigurationException {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setIgnoringElementContentWhitespace(true);
	DocumentBuilder db = dbf.newDocumentBuilder();
	return db.newDocument();
    }

    public static final Document getDocument(File file) throws SAXException, IOException, ParserConfigurationException {
	return getDocument(file, false);
    }

    public static final Document getDocument(File file, boolean validateDTD) throws SAXException, IOException, ParserConfigurationException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setValidating(validateDTD);
	factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", validateDTD);
	DocumentBuilder builder = factory.newDocumentBuilder();
	return builder.parse(new FileInputStream(file));
    }

    public static Document getDocument(InputStream inputStream) throws ParserConfigurationException, SAXException, IOException {
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document document = dBuilder.parse(inputStream);
	return document;
    }

    public static Document toDocument(String string) throws ParserConfigurationException, SAXException, IOException {
	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
	Document document = dBuilder.parse(new InputSource(new StringReader(string)));
	return document;
    }

    public static void saveDocument(Document document, File file) throws TransformerFactoryConfigurationError, TransformerException, FileNotFoundException, UnsupportedEncodingException {
	TransformerFactory tfx = TransformerFactory.newInstance();
	Transformer xformer = tfx.newTransformer();
	xformer.setOutputProperty("omit-xml-declaration", "yes");
	xformer.setOutputProperty(OutputKeys.METHOD, "xml");
	xformer.setOutputProperty(OutputKeys.INDENT, "yes");
	xformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
	OutputStream out = null;
	OutputStreamWriter writer = null;
	try {
	    out = new FileOutputStream(file);
	    writer = new OutputStreamWriter(out, "UTF-8");
	    Source source = new DOMSource(document);
	    Result result = new StreamResult(writer);
	    xformer.transform(source, result);
	} finally {
	    if (writer != null) {
		try {
		    writer.flush();
		    writer.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	    if (out != null) {
		try {
		    out.flush();
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }
}
