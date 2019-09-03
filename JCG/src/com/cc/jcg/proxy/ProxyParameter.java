package com.cc.jcg.proxy;

public class ProxyParameter {

    private final ProxyMethod method;
    private final Class<?> type;
    private final String name;
    private Object value;

    public ProxyParameter(ProxyMethod method, Class<?> type, String name, Object value) {
	this(method, type, name);
	this.value = value;
    }

    public ProxyParameter(ProxyMethod method, Class<?> type, String name) {
	super();
	this.method = method;
	this.type = type;
	this.name = name;
	value = null;
    }

    public ProxyMethod getMethod() {
	return method;
    }

    public Class<?> getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    public synchronized void setValue(Object value) {
	this.value = value;
    }

    public synchronized Object getValue() {
	return value;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ProxyParameter [");
	if (type != null) {
	    builder.append("type=");
	    builder.append(type);
	    builder.append(", ");
	}
	if (name != null) {
	    builder.append("name=");
	    builder.append(name);
	    builder.append(", ");
	}
	if (value != null) {
	    builder.append("value=");
	    builder.append(value);
	}
	builder.append("]");
	return builder.toString();
    }
}
