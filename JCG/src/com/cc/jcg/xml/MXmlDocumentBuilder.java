package com.cc.jcg.xml;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.cc.jcg.MFunctions;
import com.cc.jcg.tools.XmlVisitorElement;
import com.cc.jcg.tools.XmlVisitorException;

public class MXmlDocumentBuilder
	extends XmlVisitorElement {

    private XmlDocument document;
    private final ConcurrentMap<Node, XmlItem> items = new ConcurrentHashMap<Node, XmlItem>();
    private final Set<String> xmlNodes = new HashSet<String>();
    private final ConcurrentMap<String, XmlType> attributeTypes = new ConcurrentHashMap<String, XmlType>();

    public MXmlDocumentBuilder(File template) throws Exception {
	super(template);
    }

    public final MXmlDocumentBuilder forceAttributeType(String name, XmlType type) {
	attributeTypes.put(name, type);
	return this;
    }

    public XmlDocument guessDocument() throws XmlVisitorException {
	try {
	    items.clear();
	    xmlNodes.clear();
	    super.call();
	    return document;
	} finally {
	    items.clear();
	    xmlNodes.clear();
	}
    }

    protected String getXmlNodeName(Element element) {
	return MFunctions.camelize(element.getNodeName());
    }

    @Override
    protected final void onRootElement(Element root) {
	document = new XmlDocumentImpl(getXmlNodeName(root));
	setCurrent(document.getRoot(), root);
    }

    private void setCurrent(XmlItem node, Element element) {
	items.putIfAbsent(element, node);
	xmlNodes.add(node.getName());
	NamedNodeMap attributes = element.getAttributes();
	for (int i = 0; i < attributes.getLength(); i++) {
	    Node a = attributes.item(i);
	    String key01 = element.getNodeName() + "." + a.getNodeName();
	    XmlType type = attributeTypes.containsKey(key01) ? attributeTypes.get(key01) : null;
	    String key02 = a.getNodeName();
	    type = type == null && attributeTypes.containsKey(key02) ? attributeTypes.get(key02) : type;
	    type = type != null ? type : XmlType.STRING;
	    items.get(element).addAttribute(type, a.getNodeName());
	}
    }

    @Override
    protected final void onElement(Element element) throws XmlVisitorException {
	String name = getXmlNodeName(element);
	if (!xmlNodes.contains(name)) {
	    Node nextSibling = element.getNextSibling();
	    boolean one = nextSibling == null || nextSibling.getNextSibling() == null || !nextSibling.getNextSibling().getNodeName().equals(element.getNodeName());
	    XmlCardinality cardinality = one ? XmlCardinality.ONE : XmlCardinality.MANY;
	    String txt = element.getTextContent();
	    txt = txt.replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "");
	    boolean hasTextContent = !txt.isEmpty() && element.getChildNodes().getLength() == 1;
	    XmlNode node = items.get(element.getParentNode()).addChild(name, cardinality, hasTextContent);
	    setCurrent(node, element);
	}
    }
}
