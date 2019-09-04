package com.cc.jcg.tools.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public abstract class XmlVisitorElement
	extends XmlVisitorNode {

    public XmlVisitorElement(File file) throws SAXException, IOException, ParserConfigurationException {
	super(file);
    }

    public XmlVisitorElement(Document doc) {
	super(doc);
    }

    @Override
    protected final void onNode(Node node) throws XmlVisitorException {
	if (node instanceof Element) {
	    Element element = (Element) node;
	    try {
		onElement(element);
	    } catch (RuntimeException e) {
		e.printStackTrace();
		onException(e);
	    }
	}
    }

    protected abstract void onElement(Element element) throws XmlVisitorException;

    protected final String inspect(Element element) {
	StringBuffer s = new StringBuffer();
	s.append(level.get());
	s.append(" > " + element.getNodeName() + "[");
	NamedNodeMap attributes = element.getAttributes();
	for (int i = 0; i < attributes.getLength(); i++) {
	    s.append(attributes.item(i).getNodeName() + "=" + attributes.item(i).getNodeValue());
	    if (i < attributes.getLength() - 1) {
		s.append(",");
	    }
	}
	s.append("] = '" + element.getTextContent() + "'");
	return s.toString();
    }
}
