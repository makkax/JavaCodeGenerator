package com.cc.jcg.tools;

import java.io.InputStream;
import java.util.Scanner;

public final class ResourcesAsStream {

    public static String convertStreamToString(java.io.InputStream is) {
	Scanner scanner = null;
	try {
	    scanner = new Scanner(is);
	    Scanner s = scanner.useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	} finally {
	    if (scanner != null) {
		scanner.close();
	    }
	}
    }

    public static final InputStream getResourceAsStream(Class<?> type, String name) {
	return getResourceAsStream(type.getPackage(), name);
    }

    public static final InputStream getResourceAsStream(Package pckg, String name) {
	return getResourceAsStream(pckg.getName(), name);
    }

    public static final InputStream getResourceAsStream(String pckg, String name) {
	return getResourceAsStream(ResourcesAsStream.class, pckg, name);
    }

    public static final InputStream getResourceAsStream(Class<?> type, String pckg, String name) {
	if (name != null) {
	    try {
		return type.getResourceAsStream("/" + pckg.replace(".", "/") + "/" + name);
	    } catch (Exception e) {
		try {
		    return type.getClassLoader().getResourceAsStream("/" + pckg.replace(".", "/") + "/" + name);
		} catch (Exception e2) {
		    e.printStackTrace();
		    e2.printStackTrace();
		}
	    }
	}
	return null;
    }

    private ResourcesAsStream() {
	super();
    }
}
