package com.cc.jcg.tools;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class XmlVisitorNode
	implements XmlVisitor {

    private final Document doc;
    protected final AtomicInteger level = new AtomicInteger(0);

    public XmlVisitorNode(Document doc) {
	super();
	this.doc = doc;
    }

    public XmlVisitorNode(File file) throws SAXException, IOException, ParserConfigurationException {
	super();
	doc = XmlHelper.getDocument(file);
    }

    @Override
    public Void call() throws XmlVisitorException {
	try {
	    beforeCall();
	    Element root = doc.getDocumentElement();
	    onRootElement(root);
	    recursion(root);
	} catch (XmlVisitorException e) {
	    onException(e);
	}
	afterCall();
	return null;
    }

    protected void onRootElement(Element root) {
    }

    protected void beforeCall() throws XmlVisitorException {
    }

    protected void onException(Exception e) throws XmlVisitorException {
	throw new XmlVisitorException(e);
    }

    protected void onException(XmlVisitorException e) throws XmlVisitorException {
	e.printStackTrace();
	throw e;
    }

    protected void afterCall() throws XmlVisitorException {
    }

    protected final void recursion(Node parent) throws XmlVisitorException {
	onNode(parent);
	NodeList nodes = parent.getChildNodes();
	level.incrementAndGet();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Node node = nodes.item(i);
	    recursion(node);
	}
	level.decrementAndGet();
    }

    protected abstract void onNode(Node node) throws XmlVisitorException;
}
