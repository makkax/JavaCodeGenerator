package com.cc.jcg.xml;

import java.util.LinkedHashSet;

public interface XmlItem {

    boolean isRoot();

    String getName();

    LinkedHashSet<XmlAttribute> getAttributes();

    XmlItem addAttribute(XmlType type, String name);

    XmlItem addAttribute(String name);

    XmlItem addAttributes(String... names);

    XmlItem addBooleanAttribute(String name);

    XmlItem addBooleanAttributes(String... names);

    XmlItem addIntegerAttribute(String name);

    XmlItem addIntegerAttributes(String... names);

    LinkedHashSet<XmlNode> getChildren(XmlCardinality cardinality);

    LinkedHashSet<XmlNode> getAllChildren();

    XmlNode addChild(String name, XmlCardinality cardinality);

    XmlNode addChild(String name, XmlCardinality cardinality, boolean hasTextContent);
}
