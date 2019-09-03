package com.cc.jcg.xml;

public final class XmlDocumentImpl
	implements XmlDocument {

    private final XmlRootNode root;

    public XmlDocumentImpl(String name) {
	super();
	root = new XmlRootNodeImpl(this, name);
    }

    @Override
    public XmlRootNode getRoot() {
	return root;
    }

    @Override
    public void visit(XmlDocumentVisitor visitor) {
	visitor.on(root);
	for (XmlNode node : root.getAllChildren()) {
	    visit(node, visitor, 1);
	}
    }

    private void visit(XmlNode node, XmlDocumentVisitor visitor, int level) {
	visitor.on(level, node);
	for (XmlNode child : node.getAllChildren()) {
	    visit(child, visitor, level + 1);
	}
    }
}
