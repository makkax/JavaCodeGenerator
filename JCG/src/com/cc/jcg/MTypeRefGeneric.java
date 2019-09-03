package com.cc.jcg;

public class MTypeRefGeneric
	implements MTypeRef<String> {

    private final String generic;

    MTypeRefGeneric(String generic) {
	super();
	this.generic = generic;
    }

    @Override
    public String getRef() {
	return generic;
    }

    @Override
    public String getPckg() {
	return null;
    }

    @Override
    public String getSimpleName() {
	return generic;
    }

    @Override
    public String getQualifiedName() {
	return generic;
    }

    @Override
    public String getImport() {
	return null;
    }

    @Override
    public boolean isSerializable() {
	return false;
    }

    @Override
    public String toString() {
	return "MTypeRefGeneric [generic=" + generic + "]";
    }

    @Override
    public boolean isVoid() {
	return false;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (generic == null ? 0 : generic.hashCode());
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
	MTypeRefGeneric other = (MTypeRefGeneric) obj;
	if (generic == null) {
	    if (other.generic != null) {
		return false;
	    }
	} else if (!generic.equals(other.generic)) {
	    return false;
	}
	return true;
    }
}
