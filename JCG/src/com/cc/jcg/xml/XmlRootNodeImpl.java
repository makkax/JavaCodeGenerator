package com.cc.jcg.xml;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public final class XmlRootNodeImpl
	implements XmlRootNode {

    private final XmlDocument document;
    private final String name;
    private final LinkedHashSet<XmlAttribute> attributes;
    private final LinkedHashMap<XmlNode, XmlCardinality> children;

    public XmlRootNodeImpl(XmlDocument document, String name) {
	super();
	this.document = document;
	this.name = name;
	attributes = new LinkedHashSet<XmlAttribute>();
	children = new LinkedHashMap<XmlNode, XmlCardinality>();
    }

    @Override
    public boolean isRoot() {
	return true;
    }

    @Override
    public XmlDocument getDocument() {
	return document;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public LinkedHashSet<XmlAttribute> getAttributes() {
	return attributes;
    }

    @Override
    public LinkedHashSet<XmlNode> getAllChildren() {
	return new LinkedHashSet<XmlNode>(children.keySet());
    }

    @Override
    public LinkedHashSet<XmlNode> getChildren(XmlCardinality cardinality) {
	LinkedHashSet<XmlNode> list = new LinkedHashSet<XmlNode>();
	for (XmlNode node : children.keySet()) {
	    if (children.get(node).equals(cardinality)) {
		list.add(node);
	    }
	}
	return list;
    }

    @Override
    public XmlRootNode addAttribute(XmlType type, String name) {
	attributes.add(new XmlAttributeImpl(type, name));
	return this;
    }

    @Override
    public XmlRootNode addAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.STRING, name));
	return this;
    }

    @Override
    public XmlRootNode addAttributes(String... names) {
	for (String name : names) {
	    attributes.add(new XmlAttributeImpl(XmlType.STRING, name));
	}
	return this;
    }

    @Override
    public XmlRootNode addBooleanAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.BOOLEAN, name));
	return this;
    }

    @Override
    public XmlRootNode addBooleanAttributes(String... names) {
	for (String name : names) {
	    attributes.add(new XmlAttributeImpl(XmlType.BOOLEAN, name));
	}
	return this;
    }

    @Override
    public XmlRootNode addIntegerAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.INTEGER, name));
	return this;
    }

    @Override
    public XmlRootNode addIntegerAttributes(String... names) {
	for (String name : names) {
	    attributes.add(new XmlAttributeImpl(XmlType.INTEGER, name));
	}
	return this;
    }

    private void addChild(XmlNodeImpl node, XmlCardinality cardinality) {
	if (node.getParent() != null) {
	    throw new RuntimeException("the node " + node.getName() + " has already a parent");
	}
	node.setParent(this);
	children.put(node, cardinality);
    }

    @Override
    public XmlNode addChild(String name, XmlCardinality cardinality) {
	XmlNodeImpl node = new XmlNodeImpl(name);
	addChild(node, cardinality);
	return node;
    }

    @Override
    public XmlNode addChild(String name, XmlCardinality cardinality, boolean hasTextContent) {
	XmlNodeImpl node = new XmlNodeImpl(name, hasTextContent);
	addChild(node, cardinality);
	return node;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (document == null ? 0 : document.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	XmlRootNodeImpl other = (XmlRootNodeImpl) obj;
	if (document == null) {
	    if (other.document != null) {
		return false;
	    }
	} else if (!document.equals(other.document)) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }
}
