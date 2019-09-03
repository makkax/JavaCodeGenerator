package com.cc.jcg;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;

public class MField
	extends MAnnotated<MField>
	implements MCode<MField>, Comparable<MField> {

    public enum MFieldModifier implements MModifier {
	PUBLIC, PROTECTED, PRIVATE, DEFAULT;
    }

    public final MField makePublic() {
	return setModifier(MFieldModifier.PUBLIC);
    }

    public final MField makeProtected() {
	return setModifier(MFieldModifier.PROTECTED);
    }

    public final MField makePrivate() {
	return setModifier(MFieldModifier.PRIVATE);
    }

    public final MField makePrivateFinal() {
	return makePrivate().setFinal(true);
    }

    public final MField makeDefault() {
	return setModifier(MFieldModifier.DEFAULT);
    }

    public final MField setPublic(boolean boo) {
	if (boo) {
	    return makePublic();
	}
	return this;
    }

    public final MField setProtected(boolean boo) {
	if (boo) {
	    return makeProtected();
	}
	return this;
    }

    public final MField setPrivate(boolean boo) {
	if (boo) {
	    return makePrivate();
	}
	return this;
    }

    public final MField setDefault(boolean boo) {
	if (boo) {
	    return makeDefault();
	}
	return this;
    }

    private final MConstructable container;
    private final String name;
    private final MTypeRef type;
    private MFieldModifier modifier;
    private boolean isFinal;
    private boolean isStatic;
    private String generic;
    private MCodeGenerator<MField> generator;
    private String value;

    MField(MConstructable container, Class<?> type, String name) {
	this(container, new MTypeRefJava(type), name);
    }

    MField(MConstructable container, MType type, String name) {
	this(container, new MTypeRefModel(type), name);
    }

    MField(MConstructable container, String genericType, String name) {
	super();
	this.container = container;
	type = null;
	this.name = name;
	isFinal = false;
	modifier = MFieldModifier.PRIVATE;
	generator = this;
	isStatic = false;
	value = null;
	generic = genericType;
    }

    MField(MConstructable container, MTypeRef type, String name) {
	super();
	this.container = container;
	this.type = type;
	this.name = name;
	isFinal = false;
	modifier = MFieldModifier.PRIVATE;
	generator = this;
	isStatic = false;
	value = null;
	setGeneric("");
    }

    public final <T extends MConstructable> T getOwner() {
	return (T) container;
    }

    public final MTypeRef getType() {
	return type;
    }

    public final synchronized String getGeneric() {
	if ((generic == null || generic.trim().isEmpty()) && type instanceof MTypeRefJava) {
	    final MTypeRefJava casted = (MTypeRefJava) type;
	    if (Collection.class.isAssignableFrom(casted.getRef()) && container.getPckg().isEnsureCollectionGenerics()) {
		return "<?>";
	    }
	    if (Map.class.isAssignableFrom(casted.getRef()) && container.getPckg().isEnsureMapGenerics()) {
		return "<?, ?>";
	    }
	}
	return generic;
    }

    public final String getGenericContent() {
	String value = getGeneric();
	if (value.startsWith("<")) {
	    value = value.substring(1, value.length() - 1);
	}
	return value;
    }

    public final synchronized MField setGeneric(Class<?> type) {
	setGeneric("<" + type.getSimpleName() + ">");
	addImport(type);
	return this;
    }

    public final synchronized MField setGeneric(String generic) {
	this.generic = MFunctions.getBrackedGeneric(generic);
	return this;
    }

    public final String getName() {
	return name;
    }

    public final synchronized boolean isFinal() {
	return isFinal;
    }

    public final synchronized MField setFinal(boolean isFinal) {
	this.isFinal = isFinal;
	return this;
    }

    public final synchronized boolean isStatic() {
	return isStatic;
    }

    public final synchronized void setStatic(boolean isStatic) {
	this.isStatic = isStatic;
    }

    public final synchronized MFieldModifier getModifier() {
	return modifier;
    }

    public final synchronized MField setModifier(MFieldModifier modifier) {
	this.modifier = modifier;
	return this;
    }

    public final synchronized MField setValue(String value) {
	this.value = value;
	return this;
    }

    @Override
    public final String getPackageName() {
	return container.getPackageName();
    }

    @Override
    public final void addImport(Class<?> type) {
	container.addImport(type);
    }

    @Override
    public final void addImport(MTypeRef ref) {
	container.addImport(ref);
    }

    @Override
    public final void addImport(String fullname) {
	container.addImport(fullname);
    }

    @Override
    public final void addImport(MType type) {
	addImport(new MTypeRefModel(type));
    }

    @Override
    public final TreeSet<String> getImports() {
	final TreeSet<String> all = super.getImports();
	return all;
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MField [container=");
	builder.append(container);
	if (type != null) {
	    builder.append(", type=");
	    builder.append(type.getSimpleName().concat(getGeneric()));
	} else {
	    builder.append(", generic=");
	    builder.append(getGeneric());
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
	result = prime * result + (container == null ? 0 : container.hashCode());
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
	final MField other = (MField) obj;
	if (container == null) {
	    if (other.container != null) {
		return false;
	    }
	} else if (!container.equals(other.container)) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }

    @SuppressWarnings("unused")
    private String onEmptyNoSpace(MCodeBlock code) {
	return code.getLines().isEmpty() ? "" : " ";
    }

    private String onEmptyNoSpace(StringBuffer sb) {
	return sb.length() == 0 || sb.charAt(sb.length() - 1) == '\n' ? "" : " ";
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MField element) {
	final StringBuffer line = new StringBuffer();
	appendAnnotation(line);
	line.append(modifier.equals(MFieldModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	line.append(isStatic ? onEmptyNoSpace(line) + "static" : "");
	line.append(isFinal ? onEmptyNoSpace(line) + "final" : "");
	final String generic = getGeneric();
	if (type != null) {
	    line.append(onEmptyNoSpace(line) + type.getSimpleName().concat(generic));
	} else {
	    line.append(onEmptyNoSpace(line) + generic);
	}
	line.append(" ");
	line.append(name);
	if (value != null) {
	    line.append(" = ");
	    line.append(value);
	}
	if (!line.toString().trim().endsWith(";")) {
	    line.append(";");
	}
	final MCodeBlock block = new MCodeBlock();
	block.addLine(line);
	block.decrementTabs();
	return block;
    }

    @Override
    public final synchronized MField setGenerator(MCodeGenerator<MField> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MField> getGenerator() {
	return generator;
    }

    public final String getGetterMethodName() {
	return MFunctions.getterMethodName(type, name);
    }

    public final String getSetterMethodName() {
	return MFunctions.setterMethodName(name);
    }

    public final MMethod addGetterMethod() {
	return addGetterMethod(true);
    }

    public final MMethod addGetterMethod(boolean isSynchronized) {
	return addGetterMethod(MFunctions.getterMethodName(type, name), isSynchronized);
    }

    public final MMethod addGetterMethod(String name) {
	return addGetterMethod(name, true);
    }

    public final MMethod addGetterMethod(String name, boolean isSynchronized) {
	final MMethod method;
	if (type != null) {
	    container.addExtraImport(type);
	    method = container.addMethod(name, type, generic).setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock code = element.getCodeBlock(element);
		    code.addLine("return " + MField.this.getName() + ";");
		    return code;
		}
	    });
	} else {
	    method = container.addMethod(name, generic).setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock code = element.getCodeBlock(element);
		    code.addLine("return " + MField.this.getName() + ";");
		    return code;
		}
	    });
	}
	return method.setSynchronized(isSynchronized && !isFinal());
    }

    public final MMethod addSetterMethod() throws ClassNotFoundException {
	return addSetterMethod(MFunctions.setterMethodName(name));
    }

    public final MMethod addSetterMethod(boolean isSynchronized) throws ClassNotFoundException {
	return isFinal ? null : addSetterMethod(MFunctions.setterMethodName(name), isSynchronized);
    }

    public final MMethod addSetterMethod(String name) throws ClassNotFoundException {
	return isFinal ? null : addSetterMethod(name, true);
    }

    public final MMethod addSetterMethod(String name, boolean isSynchronized) {
	final MMethod method;
	if (type != null) {
	    container.addExtraImport(type);
	    method = container.addMethod(name, void.class, new MParameter(type, generic, this.name)).setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock code = element.getCodeBlock(element);
		    code.addLine("this." + MField.this.getName() + " = " + MField.this.getName() + ";");
		    return code;
		}
	    });
	} else {
	    method = container.addMethod(name, void.class, new MParameter(generic, this.name)).setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock code = element.getCodeBlock(element);
		    code.addLine("this." + MField.this.getName() + " = " + MField.this.getName() + ";");
		    return code;
		}
	    });
	}
	return method.setSynchronized(isSynchronized);
    }

    public static interface AccessorMethods {

	MMethod getter();

	MMethod setter();

	AccessorMethods overrides();
    }

    public final AccessorMethods addAccessorMethods() throws ClassNotFoundException {
	return addAccessorMethods(MFunctions.getterMethodName(type, name), MFunctions.setterMethodName(name));
    }

    public final AccessorMethods addAccessorMethods(boolean asFinals, boolean asSynchronized) throws ClassNotFoundException {
	AccessorMethods accessors = addAccessorMethods();
	accessors.getter().setFinal(asFinals);
	accessors.setter().setFinal(asFinals);
	accessors.getter().setSynchronized(asSynchronized);
	accessors.setter().setSynchronized(asSynchronized);
	return accessors;
    }

    public final AccessorMethods addAccessorMethods(String getterName, String setterName) throws ClassNotFoundException {
	final MMethod getter = addGetterMethod(getterName);
	getter.setFinal(true);
	final MMethod setter = addSetterMethod(setterName);
	if (setter != null) {
	    setter.setFinal(true);
	}
	return new AccessorMethods() {

	    @Override
	    public MMethod getter() {
		return getter;
	    }

	    @Override
	    public MMethod setter() {
		return setter;
	    }

	    @Override
	    public AccessorMethods overrides() {
		getter.overrides();
		setter.overrides();
		return this;
	    }
	};
    }

    @Override
    public int compareTo(MField o) {
	return getName().compareTo(o.getName());
    }
}
