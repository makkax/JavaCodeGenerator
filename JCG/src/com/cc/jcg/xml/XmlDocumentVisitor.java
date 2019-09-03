package com.cc.jcg.xml;

public interface XmlDocumentVisitor {

    void on(XmlRootNode node);

    void on(int level, XmlNode node);
}
