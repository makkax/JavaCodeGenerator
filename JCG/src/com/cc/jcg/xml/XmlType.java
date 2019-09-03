package com.cc.jcg.xml;

public interface XmlType<T> {

    Class<T> getJavaType();

    T map(String value);

    String getMapCall(String argument);

    public static final XmlTypeString STRING = new XmlTypeString();
    public static final XmlTypeInteger INTEGER = new XmlTypeInteger();
    public static final XmlTypeBoolean BOOLEAN = new XmlTypeBoolean();

    public static final class XmlTypeString
	    implements XmlType<String> {

	private XmlTypeString() {
	    super();
	}

	@Override
	public Class<String> getJavaType() {
	    return String.class;
	}

	@Override
	public String map(String value) {
	    return value;
	}

	@Override
	public String getMapCall(String argument) {
	    return argument;
	}
    }

    public static final class XmlTypeInteger
	    implements XmlType<Integer> {

	private XmlTypeInteger() {
	    super();
	}

	@Override
	public Class<Integer> getJavaType() {
	    return Integer.class;
	}

	@Override
	public Integer map(String value) {
	    try {
		if (value == null) {
		    return null;
		}
		return Integer.valueOf(value);
	    } catch (NumberFormatException e) {
		return null;
	    }
	}

	@Override
	public String getMapCall(String argument) {
	    return "XmlType.INTEGER.map(" + argument + ")";
	}
    }

    public static final class XmlTypeBoolean
	    implements XmlType<Boolean> {

	private XmlTypeBoolean() {
	    super();
	}

	@Override
	public Class<Boolean> getJavaType() {
	    return Boolean.class;
	}

	@Override
	public Boolean map(String value) {
	    if (value == null) {
		return null;
	    }
	    boolean boo = false;
	    value = value.toLowerCase();
	    boo = boo || value.equals("true");
	    boo = boo || value.equals("yes");
	    boo = boo || value.equals("1");
	    return boo;
	}

	@Override
	public String getMapCall(String argument) {
	    return "XmlType.BOOLEAN.map(" + argument + ")";
	}
    }
}
