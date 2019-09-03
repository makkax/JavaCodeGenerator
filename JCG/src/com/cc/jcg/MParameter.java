package com.cc.jcg;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

public final class MParameter
	extends MAnnotated<MParameter>
	implements MCodeGenerator<MParameter> {

    private final String name;
    private final MTypeRef type;
    private String genericType;
    private final TreeSet<String> imports = new TreeSet<String>();

    public MParameter(MType type, String name) {
	this(new MTypeRefModel(type), name);
    }

    public MParameter(MType type, String genericType, String name) {
	this(new MTypeRefModel(type), genericType, name);
    }

    public MParameter(MType type, Class<?> genericType, String name) {
	this(new MTypeRefModel(type), genericType.getSimpleName(), name);
	addImport(genericType);
    }

    public MParameter(Class<?> type, String name) {
	this(new MTypeRefJava(type), name);
    }

    public MParameter(Class<?> type, String genericType, String name) {
	this(new MTypeRefJava(type), genericType, name);
    }

    public MParameter(Class<?> type, Class<?> genericType, String name) {
	this(new MTypeRefJava(type), genericType.getSimpleName(), name);
	addImport(genericType);
    }

    public MParameter(MTypeRef type, String name) {
	this(type, "", name);
    }

    public MParameter(MTypeRef type, Class<?> genericType, String name) {
	this(type, genericType.getSimpleName(), name);
	addImport(genericType);
    }

    public MParameter(MTypeRef type, String genericType, String name) {
	super();
	this.name = name;
	this.type = type;
	setGeneric(genericType);
    }

    public MParameter(String type, String name) {
	super();
	this.name = name;
	this.type = null;
	setGeneric(type);
    }

    public MParameter(MJavaFile type) {
	this(type, type.getName());
    }

    public MParameter(MJavaFile type, String name) {
	this(new MTypeRefModel(type), name);
    }

    public MTypeRef getType() {
	return type;
    }

    public String getName() {
	return name;
    }

    public synchronized String getGenericType() {
	final MBundle bundle = MPackage.getGeneratingBundle();
	if (bundle != null && (genericType == null || genericType.trim().isEmpty()) && type instanceof MTypeRefJava) {
	    final MTypeRefJava casted = (MTypeRefJava) type;
	    if (Collection.class.isAssignableFrom(casted.getRef()) && bundle.isEnsureCollectionGenerics()) {
		return "<?>";
	    }
	    if (Map.class.isAssignableFrom(casted.getRef()) && bundle.isEnsureMapGenerics()) {
		return "<?, ?>";
	    }
	}
	return genericType;
    }

    public synchronized MParameter setGeneric(String genericType) {
	if (type == null) {
	    this.genericType = genericType.trim();
	} else {
	    this.genericType = MFunctions.getBrackedGeneric(genericType);
	}
	return this;
    }

    @Override
    public final String getPackageName() {
	return type.getPckg();
    }

    @Override
    public final TreeSet<String> getImports() {
	final TreeSet<String> all = super.getImports();
	all.addAll(imports);
	return all;
    }

    @Override
    public final void addImport(Class<?> type) {
	imports.add(type.getName());
    }

    @Override
    public final void addImport(MTypeRef ref) {
	imports.add(ref.getImport());
    }

    @Override
    public final void addImport(String fullname) {
	imports.add(fullname);
    }

    @Override
    public final void addImport(MType type) {
	addImport(new MTypeRefModel(type));
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MParameter [type=");
	if (type != null) {
	    builder.append(type.getSimpleName().concat(getGenericType()));
	} else {
	    builder.append(getGenericType());
	}
	builder.append(", name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (genericType == null ? 0 : genericType.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
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
	final MParameter other = (MParameter) obj;
	if (genericType == null) {
	    if (other.genericType != null) {
		return false;
	    }
	} else if (!genericType.equals(other.genericType)) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (type == null) {
	    if (other.type != null) {
		return false;
	    }
	} else if (!type.equals(other.type)) {
	    return false;
	}
	return true;
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MParameter element) {
	final StringBuffer line = new StringBuffer();
	appendAnnotation(line);
	final String generic = getGenericType();
	if (element.getType() != null) {
	    line.append(element.getType().getSimpleName().concat(generic));
	} else {
	    line.append(generic);
	}
	line.append(" ");
	line.append(element.getName());
	final MCodeBlock block = new MCodeBlock(line);
	block.decrementTabs();
	return block;
    }
}
