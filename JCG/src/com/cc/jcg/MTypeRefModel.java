package com.cc.jcg;

public final class MTypeRefModel
	implements MTypeRef<MType> {

    private final MType type;

    public MTypeRefModel(MType type) {
	super();
	this.type = type;
    }

    @Override
    public MType getRef() {
	return type;
    }

    @Override
    public String getPckg() {
	return type.getPackageName();
    }

    @Override
    public String getSimpleName() {
	return type.getName();
    }

    @Override
    public String getQualifiedName() {
	return type.getQualifiedName();
    }

    @Override
    public String getImport() {
	return type.getQualifiedName();
    }

    @Override
    public String toString() {
	return "MTypeRefModel [type=" + type + "]";
    }

    @Override
    public boolean isVoid() {
	return false;
    }

    @Override
    public boolean isSerializable() {
	if (type instanceof MClass) {
	    boolean b = false;
	    MClass casted = (MClass) type;
	    b = b || casted.getSuperclass() != null && casted.getSuperclass().isSerializable();
	    for (MTypeRef ref : casted.getInterfaces()) {
		b = b || ref.isSerializable();
	    }
	    return b;
	}
	if (type instanceof MInterface) {
	    boolean b = false;
	    MInterface casted = (MInterface) type;
	    for (MTypeRef ref : casted.getInterfaces()) {
		b = b || ref.isSerializable();
	    }
	    return b;
	}
	if (type instanceof MEnum) {
	    boolean b = false;
	    MEnum casted = (MEnum) type;
	    for (MTypeRef ref : casted.getInterfaces()) {
		b = b || ref.isSerializable();
	    }
	    return b;
	}
	return false;
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
	MTypeRefModel other = (MTypeRefModel) obj;
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
