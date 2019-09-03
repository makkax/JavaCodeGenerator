package com.cc.jcg;

import java.io.Serializable;

public class MTypeRefJava
	implements MTypeRef<Class<?>> {

    private final Class<?> type;

    public MTypeRefJava(Class<?> type) {
	super();
	this.type = type;
    }

    @Override
    public Class<?> getRef() {
	return type;
    }

    @Override
    public String getPckg() {
	return type.getPackage().getName();
    }

    @Override
    public String getSimpleName() {
	return type.getSimpleName();
    }

    @Override
    public String getQualifiedName() {
	return type.getName().replace("$", ".");
    }

    @Override
    public String getImport() {
	if (type.isArray()) {
	    return MFunctions.getComponentTypeName(type);
	}
	return type.getName();
    }

    @Override
    public boolean isSerializable() {
	return Serializable.class.isAssignableFrom(type);
    }

    @Override
    public String toString() {
	return "MTypeRefJava [type=" + type + "]";
    }

    @Override
    public boolean isVoid() {
	return void.class.isAssignableFrom(type) || Void.class.isAssignableFrom(type);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (type == null ? 0 : type.hashCode());
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
	MTypeRefJava other = (MTypeRefJava) obj;
	if (type == null) {
	    if (other.type != null) {
		return false;
	    }
	} else if (!type.equals(other.type)) {
	    return false;
	}
	return true;
    }
}
