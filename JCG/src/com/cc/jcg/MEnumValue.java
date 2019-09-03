package com.cc.jcg;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class MEnumValue {

    private final String name;
    private final LinkedList<Object> properties = new LinkedList<Object>();

    public MEnumValue(String name) {
	super();
	this.name = name;
    }

    public MEnumValue(String name, Object... properties) {
	this(name);
	this.properties.addAll(Arrays.asList(properties));
    }

    public MEnumValue(String name, Collection<Object> properties) {
	this(name);
	this.properties.addAll(properties);
    }

    public String getName() {
	return name;
    }

    public String getValueBuilder() {
	StringBuffer sb = new StringBuffer(name);
	if (!properties.isEmpty()) {
	    sb.append("(");
	    for (Object property : properties) {
		if (property instanceof String) {
		    sb.append("\"");
		}
		if (property instanceof Class) {
		    sb.append(((Class) property).getSimpleName() + ".class");
		} else {
		    sb.append(property);
		}
		if (property instanceof String) {
		    sb.append("\"");
		}
		sb.append(", ");
	    }
	    sb.delete(sb.length() - 2, sb.length());
	    sb.append(")");
	}
	return sb.toString();
    }

    public List<Object> getProperties() {
	return Collections.unmodifiableList(properties);
    }

    public MEnumValue removeAllProperties() {
	properties.clear();
	return this;
    }

    public MEnumValue addProperty(Object value) {
	properties.add(value);
	return this;
    }

    @Override
    public String toString() {
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
	MEnumValue other = (MEnumValue) obj;
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
