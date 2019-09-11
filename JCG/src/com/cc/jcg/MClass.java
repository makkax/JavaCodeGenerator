package com.cc.jcg;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.StandardToStringStyle;

import com.cc.jcg.MConstructor.MConstructorModifier;
import com.cc.jcg.MMethod.MMethodModifier;

public class MClass
	extends MConstructable<MClass>
	implements MCode<MClass>, MExcludable {

    private boolean generate = true;

    @Override
    public final void doNotGenerate() {
	generate = false;
    }

    @Override
    public boolean isToGenerate() {
	return generate;
    }

    public enum MClassModifier implements MModifier {
	PUBLIC, PRIVATE, DEFAULT;
    }

    public final MClass makePublic() {
	return setModifier(MClassModifier.PUBLIC);
    }

    public final MClass makePrivate() {
	return setModifier(MClassModifier.PRIVATE);
    }

    public final MClass makeDefault() {
	return setModifier(MClassModifier.DEFAULT);
    }

    private final MPackage pckg;
    private final String name;
    private final LinkedHashSet<MInnerType> innerTypes = new LinkedHashSet<MInnerType>();
    private final LinkedHashSet<MTypeRef> implementsTypes = new LinkedHashSet<MTypeRef>();
    private String superclassGeneric = "";
    private final Map<Object, String> interfaceGenerics = new HashMap<Object, String>();
    private MClassModifier modifier;
    private boolean isFinal;
    private boolean isAbstract;
    private MTypeRef extendsType;
    private String generic;
    private MCodeGenerator<MClass> generator;

    MClass(MPackage pckg, String name) {
	super();
	this.pckg = pckg;
	this.name = name;
	modifier = MClassModifier.PUBLIC;
	generator = this;
	isFinal = false;
	isAbstract = false;
	generic = "";
    }

    @Override
    public final File getSrcFile() {
	if (isTemplate()) {
	    return new File(pckg.getTemplatesDir(), name.concat(".java"));
	}
	return new File(pckg.getDir(), name.concat(".java"));
    }

    @Override
    public final MPackage getPckg() {
	return pckg;
    }

    @Override
    public final String getPackageName() {
	return pckg.getName();
    }

    public MPackage getPackage() {
	return pckg;
    }

    @Override
    public final String getName() {
	return name;
    }

    @Override
    public final String getQualifiedName() {
	return pckg.getName().concat(".").concat(name);
    }

    @Override
    public Class<?> getJavaType() throws ClassNotFoundException {
	return Class.forName(getQualifiedName());
    }

    public final synchronized String getGeneric() {
	return generic;
    }

    public final synchronized MClass setGeneric(String generic) {
	this.generic = MFunctions.getBrackedGeneric(generic);
	return this;
    }

    public final synchronized MClassModifier getModifier() {
	return modifier;
    }

    public final synchronized MClass setModifier(MClassModifier modifier) {
	this.modifier = modifier;
	return this;
    }

    public final Set<MInnerType> getInnerTypes() {
	return Collections.unmodifiableSet(innerTypes);
    }

    public final Set<MField> getMutableFields() {
	final LinkedHashSet<MField> fields = new LinkedHashSet<MField>();
	for (final MField field : this.fields) {
	    if (!field.isFinal()) {
		fields.add(field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    public MInnerClass newInnerClass(String name) {
	final MInnerClass type = new MInnerClass(pckg, name);
	innerTypes.add(type);
	return type;
    }

    public MInnerInterface newInnerInterface(String name) {
	final MInnerInterface type = new MInnerInterface(pckg, name);
	innerTypes.add(type);
	return type;
    }

    public MInnerEnum newInnerEnum(String name) {
	final MInnerEnum type = new MInnerEnum(pckg, name);
	innerTypes.add(type);
	return type;
    }

    public MClass newSubclass(String name) {
	final MClass type = pckg.newClass(name);
	type.setSuperclass(this);
	type.setGeneric(generic);
	for (final MConstructor constructor : constructors) {
	    final MConstructor sc = type.addConstructor(constructor.getParameters());
	    sc.setModifier(constructor.getModifier());
	    for (final Class<? extends Throwable> e : constructor.getExceptions()) {
		sc.throwsException(e);
	    }
	    sc.setGenerator(new MCodeGenerator<MConstructor>() {

		@Override
		public MCodeBlock getCodeBlock(MConstructor element) {
		    final MCodeBlock code = element.getCodeBlock(element);
		    code.addLine("super(" + element.getParameterNames() + ");");
		    return code;
		}
	    });
	}
	return type;
    }

    @Override
    public final void addImport(Class<?> type) {
	addImport(extraImports, type);
    }

    @Override
    public final void addImport(MTypeRef ref) {
	addImport(extraImports, ref);
    }

    @Override
    public final void addImport(String fullname) {
	addImport(extraImports, fullname);
    }

    @Override
    public final void addImport(MType type) {
	addImport(new MTypeRefModel(type));
    }

    @Override
    public final TreeSet<String> getImports() {
	final TreeSet<String> imports = super.getImports();
	for (final MTypeRef type : implementsTypes) {
	    addImport(imports, type);
	}
	if (extendsType != null) {
	    addImport(imports, extendsType);
	}
	for (final MInnerType type : innerTypes) {
	    imports.addAll(type.getImports());
	}
	MFunctions.recursiveCleanUpImports(this, imports, true);
	return imports;
    }

    public final synchronized MClass setSuperclass(MJavaFile type) {
	if (isFinal()) {
	    throw new RuntimeException("the final class " + getName() + " can not be extended");
	}
	extendsType = new MTypeRefModel(type);
	return this;
    }

    public final synchronized MClass setSuperclass(Class<?> type, Class<?> generic) {
	setSuperclass(type);
	superclassGeneric = generic.getSimpleName();
	addImport(generic);
	return this;
    }

    public final synchronized MClass setSuperclass(MJavaFile type, Class<?> generic) {
	setSuperclass(type);
	superclassGeneric = generic.getSimpleName();
	addImport(generic);
	return this;
    }

    public final synchronized MClass setSuperclass(MJavaFile type, String generic) {
	setSuperclass(type);
	superclassGeneric = generic;
	return this;
    }

    public final synchronized MClass setSuperclass(MJavaFile type, MClass generic) {
	setSuperclass(type);
	superclassGeneric = generic.getName();
	addExtraImport(generic);
	return this;
    }

    public final synchronized MClass setSuperclass(MJavaFile type, MJavaFile generic) {
	setSuperclass(type);
	superclassGeneric = generic.getName();
	addExtraImport(generic);
	return this;
    }

    public final synchronized MClass setSuperclass(MClass type) {
	if (isFinal()) {
	    throw new RuntimeException("the final class " + getName() + " can not be extended");
	}
	extendsType = new MTypeRefModel(type);
	return this;
    }

    public final synchronized MClass setSuperclass(MClass type, MJavaFile generic) {
	setSuperclass(type);
	superclassGeneric = generic.getName();
	addExtraImport(generic);
	return this;
    }

    public final synchronized MTypeRef getSuperclass() {
	return extendsType;
    }

    public final synchronized MClass setSuperclass(Class<?> type) {
	if (type != null && type.isInterface()) {
	    throw new RuntimeException(type + " is an interface");
	}
	if (type != null && Modifier.isFinal(type.getModifiers())) {
	    throw new RuntimeException(type + " is final");
	}
	extendsType = new MTypeRefJava(type);
	superclassGeneric = "";
	return this;
    }

    public final synchronized MClass setSuperclass(Class<?> type, String generic) {
	setSuperclass(type);
	superclassGeneric = generic == null ? "" : generic;
	return this;
    }

    public final synchronized MClass setSuperclass(Class<?> type, MClass generic) {
	setSuperclass(type);
	superclassGeneric = generic.getName();
	addExtraImport(generic);
	return this;
    }

    public final synchronized MClass setSuperclass(Class<?> type, MJavaFile generic) {
	setSuperclass(type);
	superclassGeneric = generic.getName();
	addExtraImport(generic);
	return this;
    }

    public final Set<MTypeRef> getInterfaces() {
	return Collections.unmodifiableSet(implementsTypes);
    }

    public final Set<MTypeRef> getAllModelInterfaces() {
	final Set<MTypeRef> all = new LinkedHashSet<MTypeRef>();
	addAllModelInterfaces(this, all);
	return all;
    }

    private void addAllModelInterfaces(MClass type, Set<MTypeRef> all) {
	if (type.getSuperclass() != null) {
	    addAllModelInterfaces(type.getSuperclass(), all);
	}
	for (final MTypeRef intfRef : type.getInterfaces()) {
	    if (!all.contains(intfRef)) {
		all.add(intfRef);
		addAllModelInterfaces(intfRef, all);
	    }
	}
    }

    private void addAllModelInterfaces(MTypeRef typeRef, Set<MTypeRef> all) {
	if (typeRef instanceof MTypeRefModel) {
	    final MType ref = ((MTypeRefModel) typeRef).getRef();
	    if (ref instanceof MClass) {
		addAllModelInterfaces((MClass) ref, all);
	    } else if (ref instanceof MInterface) {
		for (final MTypeRef intfRef : ((MInterface) ref).getInterfaces()) {
		    if (!all.contains(intfRef)) {
			all.add(intfRef);
			addAllModelInterfaces(intfRef, all);
		    }
		}
	    }
	} else if (typeRef instanceof MTypeRefJava) {
	    @SuppressWarnings("unused")
	    final Class<?> ref = ((MTypeRefJava) typeRef).getRef();
	    // not considered
	}
    }

    public MClass addInterface(MJavaFile type) {
	implementsTypes.add(new MTypeRefModel(type));
	return this;
    }

    public MClass addInterface(MJavaFile type, MClass generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic.getName());
	addExtraImport(generic);
	return this;
    }

    public MClass addInterface(MJavaFile type, Class<?> generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic.getSimpleName());
	addExtraImport(generic);
	return this;
    }

    public MClass addInterface(MJavaFile type, MJavaFile generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic.getName());
	addExtraImport(generic);
	return this;
    }

    public MClass addInterface(MInterface type) {
	implementsTypes.add(new MTypeRefModel(type));
	return this;
    }

    public final MClass addInterface(MInterface type, String generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic);
	return this;
    }

    public MClass addInterface(Class<?> type) {
	if (!type.isInterface()) {
	    throw new RuntimeException(type + " is not an interface");
	}
	implementsTypes.add(new MTypeRefJava(type));
	return this;
    }

    public final MClass addInterface(Class<?> type, String generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic);
	return this;
    }

    public MClass addInterface(Class<?> type, MClass generic) {
	addInterface(type);
	interfaceGenerics.put(type, generic.getName());
	addExtraImport(generic);
	return this;
    }

    public final synchronized boolean isFinal() {
	return isFinal;
    }

    public final synchronized MClass setFinal(boolean isFinal) {
	this.isFinal = isFinal;
	return this;
    }

    public final synchronized boolean isAbstract() {
	return isAbstract;
    }

    public final synchronized MClass setAbstract(boolean isAbstract) {
	this.isAbstract = isAbstract;
	return this;
    }

    public MMethod addMainMethod() {
	final MMethod main = addMethod("main", void.class, new MParameter(String[].class, "args"));
	main.setStatic(true);
	return main;
    }

    public List<MConstructor> overrideConstructors() {
	final List<MConstructor> createds = new ArrayList<MConstructor>();
	final MTypeRef ref = getSuperclass();
	if (ref != null) {
	    if (ref instanceof MTypeRefModel) {
		final MTypeRefModel casted = (MTypeRefModel) ref;
		if (casted.getRef() instanceof MClass) {
		    final MClass type = (MClass) casted.getRef();
		    for (final MConstructor c : type.getConstructors()) {
			final MConstructor cc = addConstructor(c.getParameters());
			createds.add(cc);
			cc.setModifier(c.getModifier());
			cc.setBlockContent("super(" + c.getParameterNames() + ");");
			for (final Class<? extends Throwable> e : c.getExceptions()) {
			    cc.throwsException(e);
			}
		    }
		} else if (casted.getRef() instanceof MJavaFile) {
		    final MJavaFile type = (MJavaFile) casted.getRef();
		    try {
			final Class<?> javaType = type.getJavaType();
			createds.addAll(generateConstructors(javaType));
		    } catch (final Exception e) {
			e.printStackTrace();
		    }
		}
	    }
	    if (ref instanceof MTypeRefJava) {
		final MTypeRefJava casted = (MTypeRefJava) ref;
		final Class<?> type = casted.getRef();
		createds.addAll(generateConstructors(type));
	    }
	}
	return createds;
    }

    public List<MConstructor> generateConstructors(Class<?> type) {
	final List<MConstructor> createds = new ArrayList<MConstructor>();
	for (final Constructor<?> jc : type.getDeclaredConstructors()) {
	    final Set<MParameter> parameters = new LinkedHashSet<MParameter>();
	    StringBuffer names = new StringBuffer();
	    final AtomicInteger index = new AtomicInteger(0);
	    int indx = 0;
	    for (final Parameter par : jc.getParameters()) {
		// TODO: generics?!
		final Class<?> jpt = par.getType();
		final String name = par.getName();
		boolean add = true;
		for (final MJavaFile jf : pckg.getJavaFiles()) {
		    if (jf.getOriginalPackage().concat("." + jf.getOriginalName()).equals(jpt.getName())) {
			final MParameter p = new MParameter(jf, name);
			parameters.add(p);
			add = false;
			break;
		    }
		}
		if (add) {
		    final MParameter p = new MParameter(jpt, name);
		    if (MFunctions.getGenericTypesCount(par) > 0) {
			p.setGeneric(MFunctions.toGenericString(p, MFunctions.getGenericType(par)));
		    }
		    parameters.add(p);
		}
		names.append(name + ", ");
		indx = indx + 1;
	    }
	    final MConstructor cc = addConstructor(parameters);
	    createds.add(cc);
	    if (Modifier.isPublic(jc.getModifiers())) {
		cc.setModifier(MConstructorModifier.PUBLIC);
	    } else if (Modifier.isProtected(jc.getModifiers())) {
		cc.setModifier(MConstructorModifier.PROTECTED);
	    } else if (Modifier.isPrivate(jc.getModifiers())) {
		cc.setModifier(MConstructorModifier.PRIVATE);
	    } else {
		cc.setModifier(MConstructorModifier.DEFAULT);
	    }
	    if (names.length() > 0) {
		names = names.delete(names.length() - 2, names.length());
	    }
	    cc.setBlockContent("super(" + names + ");");
	    for (final Class<?> e : jc.getExceptionTypes()) {
		cc.throwsException((Class<? extends Throwable>) e);
	    }
	}
	return createds;
    }

    public MMethod overrideMethod(String name) {
	final MTypeRef ref = getSuperclass();// one level only!
	if (ref != null) {
	    if (ref instanceof MTypeRefModel) {
		final MTypeRefModel casted = (MTypeRefModel) ref;
		final MClass type = (MClass) casted.getRef();
		for (final MMethod m : type.getMethods()) {
		    if (m.getName().equals(name)) {
			final MTypeRef returnType = m.getReturnType();
			final MMethod mm = addMethod(m.getName(), returnType, m.getGenericReturnType(), m.getParameters());
			mm.setModifier(m.getModifier());
			mm.setSynchronized(m.isSynchronized());
			mm.setAbstract(false);
			mm.overrides();
			returnSuper(mm);
			for (final Class<? extends Throwable> e : m.getExceptions()) {
			    mm.throwsException(e);
			}
			for (final String i : type.getImports()) {
			    final String[] ws = i.split("\\.");
			    final String w = ws[ws.length - 1];
			    if (mm.getCodeBlock(mm).getLines().toString().contains(w)) {
				extraImports.add(i);
			    }
			}
			return mm;
		    }
		}
	    }
	    if (ref instanceof MTypeRefJava) {
		final MTypeRefJava casted = (MTypeRefJava) ref;
		final Class<?> type = casted.getRef();
		for (final Method jm : type.getDeclaredMethods()) {
		    if (jm.getName().equals(name)) {
			final Set<MParameter> parameters = new LinkedHashSet<MParameter>();
			final AtomicInteger index = new AtomicInteger(0);
			for (final Class<?> jp : jm.getParameterTypes()) {
			    final String pName = "p" + index.incrementAndGet();
			    parameters.add(new MParameter(jp, pName));
			}
			final MMethod mm = addMethod(jm.getName(), jm.getReturnType(), parameters);
			// mm.setGenericReturnType("<" + jm.getGenericReturnType().toString() + ">");
			if (Modifier.isPublic(jm.getModifiers())) {
			    mm.setModifier(MMethodModifier.PUBLIC);
			}
			if (Modifier.isProtected(jm.getModifiers())) {
			    mm.setModifier(MMethodModifier.PROTECTED);
			}
			mm.setSynchronized(Modifier.isSynchronized(jm.getModifiers()));
			mm.setAbstract(false);
			mm.overrides();
			returnSuper(mm);
			for (final Class<?> e : jm.getExceptionTypes()) {
			    mm.throwsException((Class<? extends Throwable>) e);
			}
			return mm;
		    }
		}
	    }
	}
	return null;
    }

    public void returnSuper(MMethod mm) {
	if (!mm.isVoid()) {
	    mm.setReturnValue("return super." + mm.getName() + "(" + mm.getParameterNames() + ");");
	} else {
	    mm.setBlockContent("super." + mm.getName() + "(" + mm.getParameterNames() + ");");
	}
    }

    public void overrideAbstractMethods() {
	overrideMethods(true, false);
    }

    public void overrideProtecedMethods() {
	overrideMethods(false, true);
    }

    public void overrideMethods(boolean abstracts, boolean protecteds) {
	final MTypeRef ref = getSuperclass();// one level only!
	if (ref != null) {
	    if (ref instanceof MTypeRefModel) {
		final MTypeRefModel casted = (MTypeRefModel) ref;
		final MClass type = (MClass) casted.getRef();
		for (final MMethod m : type.getMethods()) {
		    if (abstracts && m.isAbstract() || protecteds && m.isProtected()) {
			final MTypeRef returnType = m.getReturnType();
			final MMethod mm = addMethod(m.getName(), returnType, m.getGenericReturnType(), m.getParameters());
			mm.setModifier(m.getModifier());
			mm.setSynchronized(m.isSynchronized());
			mm.setAbstract(false);
			mm.overrides();
			if (abstracts) {
			    returnNull(mm);
			} else {
			    returnSuper(mm);
			}
			for (final Class<? extends Throwable> e : m.getExceptions()) {
			    mm.throwsException(e);
			}
			for (final String i : type.getImports()) {
			    final String[] ws = i.split("\\.");
			    final String w = ws[ws.length - 1];
			    if (mm.getCodeBlock(mm).getLines().toString().contains(w)) {
				extraImports.add(i);
			    }
			}
		    }
		}
	    }
	    if (ref instanceof MTypeRefJava) {
		final MTypeRefJava casted = (MTypeRefJava) ref;
		final Class<?> type = casted.getRef();
		for (final Method jm : type.getMethods()) {
		    if (abstracts && Modifier.isAbstract(jm.getModifiers()) || protecteds && Modifier.isProtected(jm.getModifiers())) {
			final Set<MParameter> parameters = new LinkedHashSet<MParameter>();
			final AtomicInteger index = new AtomicInteger(0);
			for (final Class<?> jp : jm.getParameterTypes()) {
			    final String pName = "p" + index.incrementAndGet();
			    parameters.add(new MParameter(jp, pName));
			}
			final MMethod mm = addMethod(jm.getName(), jm.getReturnType(), parameters);
			if (Modifier.isPublic(jm.getModifiers())) {
			    mm.setModifier(MMethodModifier.PUBLIC);
			}
			if (Modifier.isProtected(jm.getModifiers())) {
			    mm.setModifier(MMethodModifier.PROTECTED);
			}
			mm.setSynchronized(Modifier.isSynchronized(jm.getModifiers()));
			mm.setAbstract(false);
			mm.overrides();
			if (abstracts) {
			    returnNull(mm);
			} else {
			    returnSuper(mm);
			}
			for (final Class<?> e : jm.getExceptionTypes()) {
			    mm.throwsException((Class<? extends Throwable>) e);
			}
		    }
		}
	    }
	}
    }

    private void returnNull(MMethod mm) {
	if (!mm.isVoid()) {
	    if (mm.getReturnType() instanceof MTypeRefJava) {
		final Class<?> ref = (Class<?>) mm.getReturnType().getRef();
		if (Number.class.isAssignableFrom(ref)) {
		    mm.setReturnValue("return 0;");
		} else if (Boolean.class.isAssignableFrom(ref) || boolean.class.isAssignableFrom(ref)) {
		    mm.setReturnValue("return false;");
		} else {
		    mm.setReturnValue("return null;");
		}
	    } else {
		mm.setReturnValue("return null;");
	    }
	}
    }

    public MClass generateToString(MClass type) {
	return generateToString("this");
    }

    public MClass generateToString(String argument) {
	final MMethod toString = addMethod("toString", String.class).overrides();
	toString.setReturnValue("return ReflectionToStringBuilder.toString(" + argument + ");");
	addExtraImport(ReflectionToStringBuilder.class);
	return this;
    }

    // StandardToStringStyle style = new StandardToStringStyle();
    // style.setFieldSeparator(", ");
    // style.setUseClassName(true);
    // style.setUseIdentityHashCode(false);
    // return new ReflectionToStringBuilder(getId(), style).toString();
    public MClass generateToString(String argument, String FieldSeparator, boolean UseClassName, boolean UseIdentityHashCode) {
	final MMethod toString = addMethod("toString", String.class).overrides();
	final StringBuffer rv = new StringBuffer();
	rv.append("StandardToStringStyle style = new StandardToStringStyle();\n");
	rv.append("style.setFieldSeparator(\"" + FieldSeparator + "\");\n");
	rv.append("style.setUseClassName(" + UseClassName + ");\n");
	rv.append("style.setUseIdentityHashCode(" + UseIdentityHashCode + ");\n");
	rv.append("return new ReflectionToStringBuilder(" + argument + ", style).toString();\n");
	toString.setBlockContent(rv);
	addExtraImport(StandardToStringStyle.class);
	addExtraImport(ReflectionToStringBuilder.class);
	return this;
    }

    public MClass generateToStringThatReturns(String returnValue) {
	final MMethod toString = addMethod("toString", String.class).overrides();
	returnValue = returnValue.trim() + (!returnValue.trim().endsWith(";") ? ";" : "");
	toString.setReturnValue("return " + returnValue);
	return this;
    }

    public MClass generateHashAndEquals() {
	return generateHashAndEquals(getFields(), false);
    }

    public MClass generateHashAndEquals(boolean useSuperHash) {
	return generateHashAndEquals(getFields(), useSuperHash);
    }

    public MClass generateHashAndEquals(MField... fields) {
	return generateHashAndEquals(false, fields);
    }

    public MClass generateHashAndEquals(boolean useSuperHash, MField... fields) {
	return generateHashAndEquals(Arrays.asList(fields), useSuperHash);
    }

    public MClass generateHashAndEquals(Collection<MField> fields) {
	return generateHashAndEquals(fields, false);
    }

    public MClass generateHashAndEquals(final Collection<MField> fields, final boolean useSuperHash) {
	if (fields.isEmpty()) {
	    throw new RuntimeException("invalid usage: select at least one Field to generate hash and equals Methods");
	}
	final MMethod hashCode = addMethod("hashCode", int.class).overrides();
	// ---------------------------------------------------------------------------------------------------------------
	// final int r1 = 2 * Double.valueOf(Math.random() * Integer.MAX_VALUE / 2).intValue() + 1;
	// final int r2 = 2 * Double.valueOf(Math.random() * Integer.MAX_VALUE / 2).intValue() + 1;
	// ---------------------------------------------------------------------------------------------------------------
	final int hc1 = getQualifiedName().hashCode();
	final int hc2 = hc1 * getPackageName().hashCode();
	final long r1 = Math.abs(2 * hc1 + 1);
	final long r2 = Math.abs(2 * hc2 + 1);
	// ---------------------------------------------------------------------------------------------------------------
	addExtraImport(org.apache.commons.lang3.builder.HashCodeBuilder.class);
	hashCode.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		final MCodeBlock block = element.getCodeBlock(element);
		block.addLine("HashCodeBuilder builder = new HashCodeBuilder(" + r1 + ", " + r2 + ");");
		for (final MField field : fields) {
		    block.addLine("builder.append(" + field.getName() + ");");
		}
		block.addLine("return builder.toHashCode();");
		return block;
	    }
	});
	addExtraImport(org.apache.commons.lang3.builder.EqualsBuilder.class);
	final MMethod equals = addMethod("equals", boolean.class, new MParameter(Object.class, "obj")).overrides();
	equals.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		final MCodeBlock block = element.getCodeBlock(element);
		// same type
		block.addLine("if (!(obj instanceof " + getName() + ")) {return false;}");
		// identical?
		block.addLine("if (this == obj) {return true;}");
		// cast
		block.addLine(getName() + " cst = (" + getName() + ") obj;");
		block.addLine("EqualsBuilder builder = new EqualsBuilder();");
		if (useSuperHash) {
		    block.addLine("builder.appendSuper(super.equals(obj));");
		}
		for (final MField field : fields) {
		    block.addLine("builder.append(" + field.getName() + ", cst." + field.getName() + ");");
		}
		block.addLine("return builder.isEquals();");
		return block;
	    }
	});
	return this;
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MClass [name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (pckg == null ? 0 : pckg.hashCode());
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
	final MClass other = (MClass) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (pckg == null) {
	    if (other.pckg != null) {
		return false;
	    }
	} else if (!pckg.equals(other.pckg)) {
	    return false;
	}
	return true;
    }

    protected static final String onEmptyNoSpace(StringBuffer sb) {
	return sb.length() == 0 || sb.charAt(sb.length() - 1) == '\n' ? "" : " ";
    }

    @Override
    public synchronized MCodeBlock getCodeBlock(MClass element) {
	new MGuessOverrides(this).call();
	for (final MMethod method : methods) {
	    if (isFinal) {
		method.setFinal(false);
	    }
	}
	final StringBuffer before = new StringBuffer();
	if (!(this instanceof MInnerClass)) {
	    before.append("package " + pckg.getName() + ";");
	    before.append("\n\n");
	    final TreeSet<String> imports = getImports();
	    for (final String i : imports) {
		before.append("import " + i + ";\n");
	    }
	    before.append(imports.isEmpty() ? "" : "\n");
	}
	appendAnnotation(before);
	before.append(modifier.equals(MClassModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	afterModifier(before);
	before.append(isFinal ? onEmptyNoSpace(before) + "final" : "");
	before.append(isAbstract ? onEmptyNoSpace(before) + "abstract" : "");
	before.append(onEmptyNoSpace(before) + "class");
	before.append(onEmptyNoSpace(before) + element.getName());
	if (generic != null && generic.length() > 2) {
	    before.append(generic);
	}
	if (extendsType != null) {
	    before.append("\n");
	    before.append(MCodeBlock.tabs(2) + "extends " + extendsType.getSimpleName().concat(MFunctions.getBrackedGeneric(superclassGeneric)));
	}
	if (!implementsTypes.isEmpty()) {
	    before.append("\n");
	    before.append(MCodeBlock.tabs(2) + "implements ");
	    final AtomicBoolean isFirst = new AtomicBoolean(true);
	    for (final MTypeRef type : implementsTypes) {
		before.append(isFirst.getAndSet(false) ? "" : ", ");
		before.append(type.getSimpleName().concat(MFunctions.getBrackedGeneric(interfaceGenerics.get(type.getRef()))));
	    }
	}
	before.append(" {");
	final String after = "}";
	final MCodeBlock inner = new MCodeBlock(before, after);
	final boolean isSerializable = isSerializable();
	if (isSerializable) {
	    inner.addEmptyLine();
	    inner.addLine("private static final long serialVersionUID = 1L;");
	}
	final AtomicBoolean hasStaticFields = new AtomicBoolean(false);
	for (final MField field : fields) {
	    if (field.isStatic()) {
		if (!hasStaticFields.getAndSet(true)) {
		    inner.addEmptyLine();
		}
		inner.addLines(field.getGenerator().getCodeBlock(field).getLines());
	    }
	}
	final AtomicBoolean hasStaticMethods = new AtomicBoolean(false);
	final MMethod last = methods.isEmpty() ? null : new LinkedList<MMethod>(methods).getLast();
	for (final MMethod method : methods) {
	    if (method.isStatic()) {
		if (!hasStaticMethods.getAndSet(true)) {
		    inner.addEmptyLine();
		}
		inner.addLines(method.getGenerator().getCodeBlock(method).getLines());
		if (!last.equals(method)) {
		    inner.addEmptyLine();
		}
	    }
	}
	final AtomicBoolean hasFields = new AtomicBoolean(false);
	for (final MField field : fields) {
	    if (!field.isStatic()) {
		if (!hasFields.getAndSet(true)) {
		    inner.addEmptyLine();
		}
		inner.addLines(field.getGenerator().getCodeBlock(field).getLines());
	    }
	}
	for (final MConstructor constructor : constructors) {
	    inner.addEmptyLine();
	    inner.addLines(constructor.getGenerator().getCodeBlock(constructor).getLines());
	}
	for (final MInnerType innerType : innerTypes) {
	    inner.addEmptyLine();
	    inner.addLines(innerType.getGenerator().getCodeBlock(innerType).getLines());
	}
	for (final MMethod method : methods) {
	    if (!method.isStatic()) {
		inner.addEmptyLine();
		inner.addLines(method.getGenerator().getCodeBlock(method).getLines());
	    }
	}
	return inner;
    }

    public final boolean isSerializable() {
	boolean r = implementsTypes.contains(Serializable.class);
	for (final MTypeRef type : implementsTypes) {
	    r = r || type.isSerializable();
	}
	if (extendsType != null) {
	    r = r || extendsType.isSerializable();
	}
	return r;
    }

    protected void afterModifier(StringBuffer before) {
    }

    @Override
    public final synchronized MClass setGenerator(MCodeGenerator<MClass> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MClass> getGenerator() {
	return generator;
    }
}
