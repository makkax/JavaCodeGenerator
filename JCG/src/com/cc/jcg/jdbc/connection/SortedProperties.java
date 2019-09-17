package com.cc.jcg.jdbc.connection;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class SortedProperties
	extends Properties {

    private final boolean sorted;

    public SortedProperties() {
	this(true);
    }

    public SortedProperties(boolean sorted) {
	super();
	this.sorted = sorted;
    }

    @Override
    public Enumeration keys() {
	if (sorted) {
	    Enumeration keysEnum = super.keys();
	    Vector<String> keyList = new Vector<String>();
	    while (keysEnum.hasMoreElements()) {
		keyList.add((String) keysEnum.nextElement());
	    }
	    Collections.sort(keyList);
	    return keyList.elements();
	} else {
	    return super.keys();
	}
    }
}