package com.cc.jcg.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public final class ProxyMethod {

    private final Class<?> returnType;
    private final String name;
    private final List<ProxyParameter> parameters;
    private Object value;

    public ProxyMethod(Class<?> type, int pm) {
	this(getMethodAnnotatedWith(type, pm));
    }

    private static final ConcurrentMap<String, Method> METHODS = new ConcurrentHashMap<String, Method>();

    private static Method getMethodAnnotatedWith(Class<?> type, int pm) {
	String key = type.getName().concat("-").concat(String.valueOf(pm));
	if (!METHODS.containsKey(key)) {
	    for (Method method : type.getDeclaredMethods()) {
		if (method.isAnnotationPresent(PM.class) && method.getAnnotation(PM.class).value() == pm) {
		    METHODS.put(key, method);
		    return method;
		}
	    }
	}
	return METHODS.get(key);
    }

    public ProxyMethod(Method method) {
	this(method.getReturnType(), method.getName());
	int i = 0;
	Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	String[] parameterNames = paranamer.lookupParameterNames(method, false);
	for (Class<?> type : method.getParameterTypes()) {
	    String name = parameterNames.length > 0 ? parameterNames[i] : "arg" + i;
	    addParameter(type, name);
	    i = i + 1;
	}
    }

    public ProxyMethod(Class<?> returnType, String name) {
	super();
	this.returnType = returnType;
	this.name = name;
	parameters = new ArrayList<ProxyParameter>();
    }

    public Class<?> getReturnType() {
	return returnType;
    }

    public String getName() {
	return name;
    }

    public List<ProxyParameter> getParameters() {
	return parameters;
    }

    public ProxyParameter getParameter(String name) {
	for (ProxyParameter parameter : parameters) {
	    if (parameter.getName().equals(name)) {
		return parameter;
	    }
	}
	return null;
    }

    public void addParameter(Class<?> type, String name) {
	addParameter(this, type, name, null);
    }

    public void addParameter(Class<?> type, String name, Object value) {
	addParameter(this, type, name, value);
    }

    private void addParameter(ProxyMethod method, Class<?> type, String name, Object value) {
	parameters.add(new ProxyParameter(method, type, name, value));
    }

    public synchronized Object getValue() {
	return value;
    }

    public synchronized void setValue(Object value) {
	this.value = value;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("ProxyMethod [");
	if (returnType != null) {
	    builder.append("returnType=");
	    builder.append(returnType);
	    builder.append(", ");
	}
	if (name != null) {
	    builder.append("name=");
	    builder.append(name);
	    builder.append(", ");
	}
	if (parameters != null) {
	    builder.append("parameters=");
	    builder.append(parameters);
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
