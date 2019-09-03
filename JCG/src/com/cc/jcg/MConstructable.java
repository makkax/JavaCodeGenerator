package com.cc.jcg;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

import com.cc.jcg.MMethod.MMethodModifier;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

@SuppressWarnings("rawtypes")
abstract class MConstructable<T extends MType>
	extends MAnnotated<T>
	implements MType {

    // ----------------------------------------------------------------------------------------------------------------------
    // template
    // ----------------------------------------------------------------------------------------------------------------------
    private boolean template;

    @Override
    public final synchronized T setTemplate(boolean boo) {
	template = boo;
	if (template) {
	    addAnnotation(new MTemplate() {

		@Override
		public Class<? extends Annotation> annotationType() {
		    return MTemplate.class;
		}
	    });
	}
	return (T) this;
    }

    @Override
    public final synchronized boolean isTemplate() {
	return template;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // MFields
    // ----------------------------------------------------------------------------------------------------------------------
    protected final LinkedHashSet<MField> fields = new LinkedHashSet<MField>();

    public final Set<MField> getFields() {
	return Collections.unmodifiableSet(fields);
    }

    public final int getFieldsPosition(MField field) {
	return new LinkedList<MField>(fields).indexOf(field);
    }

    public final void sortFieldsAlphabetically() {
	final List<MField> copy = new ArrayList<MField>(fields);
	Collections.sort(copy, new Comparator<MField>() {

	    @Override
	    public int compare(MField o1, MField o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	fields.clear();
	fields.addAll(copy);
    }

    public final MField getFieldByName(String name) {
	for (final MField field : fields) {
	    if (field.getName().equals(name)) {
		return field;
	    }
	}
	return null;
    }

    public final MField addField(MField field) {
	final MField copy = this.addField(field.getType(), field.getName());
	copy.setGeneric(field.getGeneric());
	copy(field, copy);
	fields.add(copy);
	return copy;
    }

    private static void copy(MField field, MField f) {
	f.setStatic(field.isStatic());
	f.setFinal(field.isFinal());
	f.setModifier(field.getModifier());
    }

    public final MField moveFieldAtEnd(MField field) {
	return moveField(field, fields.size() - 1);
    }

    public final MField moveFieldAtBegin(MField field) {
	return moveField(field, 0);
    }

    public final MField moveField(MField field, int postion) {
	final LinkedList<MField> copy = new LinkedList<MField>(fields);
	copy.remove(field);
	fields.clear();
	final AtomicInteger i = new AtomicInteger(0);
	final boolean added = false;
	for (final MField f : copy) {
	    if (postion == i.getAndIncrement()) {
		fields.add(field);
	    }
	    fields.add(f);
	}
	if (!added) {
	    fields.add(field);
	}
	return field;
    }

    public MField addField(Class<?> type, String name) {
	final MField field = new MField(this, type, name);
	fields.add(field);
	return field;
    }

    public MField addField(MType type, String name) {
	final MField field = new MField(this, type, name);
	fields.add(field);
	return field;
    }

    public MField addField(MTypeRef type, String name) {
	final MField field = new MField(this, type, name);
	fields.add(field);
	return field;
    }

    public MField addField(String generic, String name) {
	final MField field = new MField(this, generic, name);
	fields.add(field);
	return field;
    }

    public MField addStaticField(Class<?> type, String name) {
	final MField field = addField(type, name);
	field.makePrivate();
	field.setStatic(true);
	field.setFinal(true);
	return field;
    }

    public MField addStaticField(MType type, String name) {
	final MField field = addField(type, name);
	field.makePrivate();
	field.setStatic(true);
	field.setFinal(true);
	return field;
    }

    public MField addStaticField(MTypeRef type, String name) {
	final MField field = addField(type, name);
	field.makePrivate();
	field.setStatic(true);
	field.setFinal(true);
	return field;
    }

    public final Set<MField> getFinalFields() {
	final LinkedHashSet<MField> fields = new LinkedHashSet<MField>();
	for (final MField field : this.fields) {
	    if (!field.isStatic() && field.isFinal()) {
		fields.add(field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    public final T removeAllFields() {
	fields.clear();
	return (T) this;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // MConstructor
    // ----------------------------------------------------------------------------------------------------------------------
    protected final LinkedHashSet<MConstructor> constructors = new LinkedHashSet<MConstructor>();

    public final Set<MConstructor> getConstructors() {
	return Collections.unmodifiableSet(constructors);
    }

    public final MConstructor addConstructor(MParameter... parameters) {
	return addConstructor(Arrays.asList(parameters));
    }

    public final T removeAllConstructors() {
	constructors.clear();
	return (T) this;
    }

    public MConstructor addConstructor(Collection<MParameter> parameters) {
	final MParameter[] array = parameters.toArray(new MParameter[parameters.size()]);
	final MConstructor constructor = new MConstructor(this, array);
	constructors.remove(constructor);
	constructors.add(constructor);
	return constructor;
    }

    public final MConstructor addFinalFieldsConstructor() {
	return addConstructor(getFinalFields());
    }

    public MConstructor addFieldsConstructor(MField... fields) {
	return addConstructor(new LinkedHashSet<MField>(Arrays.asList(fields)));
    }

    public MConstructor addConstructor(Set<MField> fields) {
	final LinkedHashSet<MParameter> parameters = new LinkedHashSet<MParameter>();
	for (final MField field : fields) {
	    final MParameter parameter = new MParameter(field.getType(), field.getName());
	    parameter.setGeneric(field.getGeneric());
	    parameters.add(parameter);
	}
	final MParameter[] array = parameters.toArray(new MParameter[parameters.size()]);
	final MConstructor constructor = new MConstructor(this, array);
	constructor.setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		final MCodeBlock code = element.getCodeBlock(element);
		if (!(MConstructable.this instanceof MEnum)) {
		    code.addLine("super();");
		}
		for (final MParameter parameter : parameters) {
		    code.addLine("this." + parameter.getName() + " = " + parameter.getName() + ";");
		}
		return code;
	    }
	});
	constructors.remove(constructor);
	constructors.add(constructor);
	return constructor;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // MMethods
    // ----------------------------------------------------------------------------------------------------------------------
    protected final LinkedHashSet<MMethod> methods = new LinkedHashSet<MMethod>();

    @Override
    public final Set<MMethod> getMethods() {
	return Collections.unmodifiableSet(methods);
    }

    public final T removeAllMethods() {
	methods.clear();
	return (T) this;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    public final MMethod addStaticMethod(String name, Class<?> returnType, MParameter... parameters) {
	return addMethod(name, returnType, parameters).setStatic(true);
    }

    public final MMethod addStaticMethod(String name, Class<?> returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters).setStatic(true);
    }

    public final MMethod addStaticMethod(String name, MType returnType, MParameter... parameters) {
	return addMethod(name, returnType, parameters).setStatic(true);
    }

    public final MMethod addStaticMethod(String name, MType returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters).setStatic(true);
    }

    public final MMethod addStaticMethod(String name, String genericReturnType, MParameter... parameters) {
	return addMethod(name, genericReturnType, parameters).setStatic(true);
    }

    public final MMethod addStaticMethod(String name, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, genericReturnType, parameters).setStatic(true);
    }

    // ----------------------------------------------------------------------------------------------------------------------
    public final MMethod addMethod(Method method) {
	final Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	final String[] parameterNames = paranamer.lookupParameterNames(method, false);
	final MParameter[] parameters = new MParameter[method.getParameterTypes().length];
	int i = 0;
	for (final Class<?> type : method.getParameterTypes()) {
	    final String name = parameterNames.length > 0 ? parameterNames[i] : "arg" + i;
	    parameters[i] = new MParameter(type, name);
	    if (method.getGenericParameterTypes()[i] instanceof ParameterizedType) {
		String generic = ((ParameterizedType) method.getGenericParameterTypes()[i]).toString();
		generic = generic.substring(generic.indexOf("<"));
		parameters[i].setGeneric(generic);
	    }
	    i = i + 1;
	}
	final MMethod copy = addMethod(method.getName(), method.getReturnType(), parameters);
	if (method.getGenericReturnType() instanceof ParameterizedType) {
	    String generic = ((ParameterizedType) method.getGenericReturnType()).toString();
	    generic = generic.substring(generic.indexOf("<"));
	    copy.setGenericReturnType(generic);
	}
	// generics?!
	return copy;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    public final MMethod addMethod(MMethod method) {
	final MMethod copy = addMethod(method.getName(), method.getReturnType(), method.getParameters());
	if (method.getGenericReturnType() != null) {
	    copy.setGenericReturnType(method.getGenericReturnType());
	}
	for (final String imp : method.getImports()) {
	    copy.addImport(imp);
	}
	decorate(method, copy);
	return copy;
    }

    private void decorate(MMethod method, MMethod m) {
	m.setStatic(method.isStatic());
	if (m.getOwner() instanceof MClass && method.getOwner() instanceof MInterface) {
	    m.makePublic();
	    m.setSynchronized(method.isSynchronized());
	} else {
	    m.setAbstract(method.isAbstract());
	    m.setModifier(method.getModifier());
	    m.setFinal(method.isFinal());
	    m.setSynchronized(method.isSynchronized());
	}
    }

    // ----------------------------------------------------------------------------------------------------------------------
    public final MMethod moveMethod(MMethod method, int postion) {
	final LinkedList<MMethod> copy = new LinkedList<MMethod>(methods);
	copy.remove(method);
	methods.clear();
	final AtomicInteger i = new AtomicInteger(0);
	final boolean added = false;
	for (final MMethod m : copy) {
	    if (postion == i.getAndIncrement()) {
		methods.add(method);
	    }
	    methods.add(m);
	}
	if (!added) {
	    methods.add(method);
	}
	return method;
    }

    public final MMethod addMethod(String name, Class<?> returnType, MParameter... parameters) {
	return addMethod(name, returnType, Arrays.asList(parameters));
    }

    public final MMethod addMethod(String name, Class<?> returnType, Collection<MParameter> parameters) {
	return addMethod(name, new MTypeRefJava(returnType), "", parameters);
    }

    public final MMethod addMethod(String name, Class<?> returnType, String genericReturnType, MParameter... parameters) {
	return addMethod(name, returnType, genericReturnType, Arrays.asList(parameters));
    }

    public final MMethod addMethod(String name, Class<?> returnType, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters).setGenericReturnType(genericReturnType);
    }

    public final MMethod addMethod(String name, MType returnType, MParameter... parameters) {
	return addMethod(name, returnType, Arrays.asList(parameters));
    }

    public final MMethod addMethod(String name, MType returnType, Collection<MParameter> parameters) {
	return addMethod(name, new MTypeRefModel(returnType), "", parameters);
    }

    public final MMethod addMethod(String name, MType returnType, String genericReturnType, MParameter... parameters) {
	return addMethod(name, returnType, genericReturnType, Arrays.asList(parameters));
    }

    public final MMethod addMethod(String name, MType returnType, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters).setGenericReturnType(genericReturnType);
    }

    public final MMethod addMethod(String name, String genericReturnType, MParameter... parameters) {
	return addMethod(name, genericReturnType, Arrays.asList(parameters));
    }

    public final MMethod addMethod(String name, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, new MTypeRefGeneric(genericReturnType), "", parameters);
    }

    public MMethod addMethod(String name, MTypeRef returnType) {
	return addMethod(name, returnType, "");
    }

    public MMethod addMethod(String name, MTypeRef returnType, String genericReturnType) {
	final MMethod method = new MMethod(this, name, returnType, genericReturnType);
	method.setModifier(MMethodModifier.PUBLIC);
	for (final MMethod m : methods) {
	    if (m.equals(method)) {
		return m;
	    }
	}
	methods.add(method);
	return method;
    }

    public MMethod addMethod(String name, MTypeRef returnType, MParameter... parameters) {
	return addMethod(name, returnType, null, Arrays.asList(parameters));
    }

    public MMethod addMethod(String name, MTypeRef returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, null, parameters);
    }

    public MMethod addMethod(String name, MTypeRef returnType, String genericReturnType, MParameter... parameters) {
	return addMethod(name, returnType, genericReturnType, Arrays.asList(parameters));
    }

    public MMethod addMethod(String name, MTypeRef returnType, String genericReturnType, Collection<MParameter> parameters) {
	final MParameter[] array = parameters.toArray(new MParameter[parameters.size()]);
	final MMethod method = new MMethod(this, name, returnType, genericReturnType, array);
	method.setModifier(MMethodModifier.PUBLIC);
	for (final MMethod m : methods) {
	    if (m.equals(method)) {
		return m;
	    }
	}
	methods.add(method);
	return method;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    public final MMethod addAbstractMethod(String name, Class<?> returnType, MParameter... parameters) {
	return addAbstractMethod(name, returnType, Arrays.asList(parameters));
    }

    public final MMethod addAbstractMethod(String name, Class<?> returnType, Collection<MParameter> parameters) {
	return addAbstractMethod(name, new MTypeRefJava(returnType), "", parameters);
    }

    public final MMethod addAbstractMethod(String name, MType returnType, MParameter... parameters) {
	return addAbstractMethod(name, returnType, Arrays.asList(parameters));
    }

    public final MMethod addAbstractMethod(String name, MType returnType, Collection<MParameter> parameters) {
	return addAbstractMethod(name, new MTypeRefModel(returnType), "", parameters);
    }

    public final MMethod addAbstractMethod(String name, String genericReturnType, MParameter... parameters) {
	return addAbstractMethod(name, genericReturnType, Arrays.asList(parameters));
    }

    public final MMethod addAbstractMethod(String name, String genericReturnType, Collection<MParameter> parameters) {
	return addAbstractMethod(name, new MTypeRefGeneric(genericReturnType), "", parameters);
    }

    public MMethod addAbstractMethod(String name, MTypeRef returnType, String genericReturnType, Collection<MParameter> parameters) {
	final MParameter[] array = parameters.toArray(new MParameter[parameters.size()]);
	final MAbstractMethod method = new MAbstractMethod(this, name, returnType, genericReturnType, array);
	for (final MMethod m : methods) {
	    if (m.equals(method)) {
		return m;
	    }
	}
	methods.add(method);
	return method;
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // imports
    // ----------------------------------------------------------------------------------------------------------------------
    protected final TreeSet<String> extraImports = new TreeSet<String>();

    @Override
    public final synchronized T addExtraImport(Class<?> type) {
	addImport(extraImports, type);
	return (T) this;
    }

    public final synchronized T addExtraImport(MType type) {
	addImport(extraImports, new MTypeRefModel(type));
	return (T) this;
    }

    @Override
    public final synchronized T addExtraImport(MTypeRef type) {
	addImport(extraImports, type);
	return (T) this;
    }

    @Override
    public final T addExtraImport(String fullname) {
	addImport(extraImports, fullname);
	return (T) this;
    }

    public final synchronized T addExtraImports(Class<?>... types) {
	for (final Class<?> type : types) {
	    addExtraImport(type);
	}
	return (T) this;
    }

    public final synchronized T addExtraImports(MType... types) {
	for (final MType type : types) {
	    addExtraImport(type);
	}
	return (T) this;
    }

    public final synchronized T addExtraImports(MTypeRef... types) {
	for (final MTypeRef type : types) {
	    addExtraImport(type);
	}
	return (T) this;
    }

    @Override
    public TreeSet<String> getImports() {
	final TreeSet<String> imports = super.getImports();
	for (final MField field : fields) {
	    addImport(imports, field.getType());
	    for (final String fImport : field.getImports()) {
		extraImports.add(fImport);
	    }
	}
	for (final MConstructor constructor : constructors) {
	    for (final MParameter parameter : constructor.getParameters()) {
		addImport(imports, parameter.getType());
		addImport(imports, parameter.getImports());
	    }
	    for (final Class<? extends Throwable> e : constructor.getExceptions()) {
		addImport(imports, e);
	    }
	    for (final String cImport : constructor.getImports()) {
		extraImports.add(cImport);
	    }
	}
	for (final MMethod method : methods) {
	    for (final String i : method.getImports()) {
		imports.add(i);
	    }
	    if (!method.isVoid()) {
		addImport(imports, method.getReturnType());
	    }
	    for (final Class<? extends Throwable> e : method.getExceptions()) {
		addImport(imports, e);
	    }
	    for (final MParameter parameter : method.getParameters()) {
		addImport(imports, parameter.getType());
		addImport(imports, parameter.getImports());
	    }
	}
	imports.addAll(extraImports);
	return imports;
    }
}
