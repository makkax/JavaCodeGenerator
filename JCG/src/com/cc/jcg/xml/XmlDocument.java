package com.cc.jcg.xml;

public interface XmlDocument {

    XmlRootNode getRoot();

    void visit(XmlDocumentVisitor visitor);
}
