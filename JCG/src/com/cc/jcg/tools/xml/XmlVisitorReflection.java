package com.cc.jcg.tools.xml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XmlVisitorReflection
	extends XmlVisitorElement {

    // private static final Logger logger = Logger.getLogger(XmlVisitorReflection.class);
    public XmlVisitorReflection(File file) throws SAXException, IOException, ParserConfigurationException {
	super(file);
    }

    public XmlVisitorReflection(Document doc) {
	super(doc);
    }

    protected Set<String> getMethodNamePatternsFor(Element element) {
	Set<String> names = new HashSet<String>();
	String nodeName = element.getNodeName();
	names.add(nodeName);
	names.add(nodeName.toLowerCase());
	names.add(nodeName.toUpperCase());
	names.add("visit" + nodeName.substring(0, 1).toUpperCase().concat(nodeName.substring(1)));
	names.add("on" + nodeName.substring(0, 1).toUpperCase().concat(nodeName.substring(1)));
	return names;
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected final void onElement(Element element) throws XmlVisitorException {
	try {
	    if (element != null) {
		Class[] types = new Class[] { Element.class };
		Object[] args = new Element[] { element };
		for (String name : getMethodNamePatternsFor(element)) {
		    try {
			Method method = getClass().getDeclaredMethod(name, types);
			method.setAccessible(true);
			method.invoke(this, args);
			break;
		    } catch (NoSuchMethodException e1) {
			// logger.debug("method " + name + "(" + Element.class.getSimpleName() + " arg) not found");
		    }
		}
	    }
	} catch (SecurityException e) {
	    onException(e);
	} catch (IllegalArgumentException e) {
	    onException(e);
	} catch (IllegalAccessException e) {
	    onException(e);
	} catch (InvocationTargetException e) {
	    onException(e);
	}
    }
}
