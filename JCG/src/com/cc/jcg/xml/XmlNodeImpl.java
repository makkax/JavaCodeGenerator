package com.cc.jcg.xml;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public final class XmlNodeImpl
	implements XmlNode {

    private final String name;
    private final LinkedHashSet<XmlAttribute> attributes;
    private XmlItem parent;
    private final LinkedHashMap<XmlNode, XmlCardinality> children;
    private final boolean hasTextContent;

    public XmlNodeImpl(String name) {
	this(name, false);
    }

    public XmlNodeImpl(String name, boolean hasTextContent) {
	super();
	this.name = name;
	attributes = new LinkedHashSet<XmlAttribute>();
	children = new LinkedHashMap<XmlNode, XmlCardinality>();
	this.hasTextContent = hasTextContent;
    }

    @Override
    public boolean isRoot() {
	return false;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public LinkedHashSet<XmlAttribute> getAttributes() {
	return attributes;
    }

    synchronized void setParent(XmlItem parent) {
	this.parent = parent;
    }

    @Override
    public synchronized XmlItem getParent() {
	return parent;
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
    public boolean hasTextContent() {
	return hasTextContent;
    }

    @Override
    public XmlNode addAttribute(XmlType type, String name) {
	attributes.add(new XmlAttributeImpl(type, name));
	return this;
    }

    @Override
    public XmlNode addAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.STRING, name));
	return this;
    }

    @Override
    public XmlNode addAttributes(String... names) {
	for (String name : names) {
	    attributes.add(new XmlAttributeImpl(XmlType.STRING, name));
	}
	return this;
    }

    @Override
    public XmlNode addBooleanAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.BOOLEAN, name));
	return this;
    }

    @Override
    public XmlNode addBooleanAttributes(String... names) {
	for (String name : names) {
	    attributes.add(new XmlAttributeImpl(XmlType.BOOLEAN, name));
	}
	return this;
    }

    @Override
    public XmlNode addIntegerAttribute(String name) {
	attributes.add(new XmlAttributeImpl(XmlType.INTEGER, name));
	return this;
    }

    @Override
    public XmlNode addIntegerAttributes(String... names) {
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
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (parent == null ? 0 : parent.hashCode());
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
	XmlNodeImpl other = (XmlNodeImpl) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (parent == null) {
	    if (other.parent != null) {
		return false;
	    }
	} else if (!parent.equals(other.parent)) {
	    return false;
	}
	return true;
    }
}
