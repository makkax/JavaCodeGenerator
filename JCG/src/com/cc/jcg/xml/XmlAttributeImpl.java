package com.cc.jcg.xml;

public final class XmlAttributeImpl
	implements XmlAttribute {

    private final XmlType<?> type;
    private final String name;

    XmlAttributeImpl(XmlType<?> type, String name) {
	super();
	this.type = type;
	this.name = name;
    }

    @Override
    public <T> XmlType<T> getType() {
	return (XmlType<T>) type;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (name == null ? 0 : name.hashCode());
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
	XmlAttributeImpl other = (XmlAttributeImpl) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }
}
