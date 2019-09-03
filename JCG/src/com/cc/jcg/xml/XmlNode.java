package com.cc.jcg.xml;

public interface XmlNode
	extends XmlItem {

    XmlItem getParent();

    boolean hasTextContent();
}
