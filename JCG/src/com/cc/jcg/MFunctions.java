package com.cc.jcg;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
import java.util.function.BiConsumer;

import org.apache.commons.lang3.tuple.Pair;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class MFunctions {

    private static final List<Class<?>> PRIMITIVE_TYPES = new ArrayList<Class<?>>();
    static {
	PRIMITIVE_TYPES.add(Object.class);
	PRIMITIVE_TYPES.add(String.class);
	PRIMITIVE_TYPES.add(Integer.class);
	PRIMITIVE_TYPES.add(Long.class);
	PRIMITIVE_TYPES.add(Double.class);
	PRIMITIVE_TYPES.add(Float.class);
	PRIMITIVE_TYPES.add(Short.class);
	PRIMITIVE_TYPES.add(Boolean.class);
	PRIMITIVE_TYPES.add(int.class);
	PRIMITIVE_TYPES.add(long.class);
	PRIMITIVE_TYPES.add(double.class);
	PRIMITIVE_TYPES.add(float.class);
	PRIMITIVE_TYPES.add(short.class);
	PRIMITIVE_TYPES.add(boolean.class);
    }

    public static Class<?> toNotPrimitiveType(Class<?> type) {
	if (boolean.class.equals(type)) {
	    return Boolean.class;
	}
	return type;
    }

    static final boolean isPrimitive(String path) {
	if (path == null) {
	    return false;
	}
	for (final Class<?> type : PRIMITIVE_TYPES) {
	    if (path.equals(type.getSimpleName()) || path.equals(type.getName())) {
		return true;
	    }
	}
	return false;
    }

    static final boolean isPrimitive(Class<?> type) {
	if (type == null) {
	    return false;
	}
	return type.isPrimitive() || PRIMITIVE_TYPES.contains(type) || type.getPackage() != null && type.getPackage().getName().equals("java.lang");
    }

    static final void recursiveCleanUpImports(MType type, TreeSet<String> imports, boolean root) {
	if (root) {
	    MFunctions.cleanUpImports(type.getPackageName(), imports);
	} else {
	    MFunctions.cleanUpInnerClassImports(type.getQualifiedName(), imports);
	}
	if (type instanceof MClass) {
	    recursiveCleanUpImports((MClass) type, imports);
	} else if (type instanceof MInterface) {
	    recursiveCleanUpImports((MInterface) type, imports);
	}
    }

    private static final void cleanUpImports(String pckg, TreeSet<String> imports) {
	for (final String path : new HashSet<String>(imports)) {
	    if (isPrimitive(path)) {
		imports.remove(path);
	    } else if (path.equals(pckg) || path.startsWith(pckg) && path.charAt(pckg.length()) == '.') {
		if (path.split("\\.").length == pckg.split("\\.").length + 1) {
		    imports.remove(path);
		}
	    }
	}
    }

    private static final void recursiveCleanUpImports(MClass type, TreeSet<String> imports) {
	if (type != null) {
	    MFunctions.cleanUpInnerClassImports(type.getQualifiedName(), imports);
	    recursiveCleanUpImports(type.getSuperclass(), imports);
	    for (final MTypeRef intf : type.getInterfaces()) {
		recursiveCleanUpImports(intf, imports);
	    }
	}
    }

    private static final void recursiveCleanUpImports(MInterface type, TreeSet<String> imports) {
	if (type != null) {
	    MFunctions.cleanUpInnerClassImports(type.getQualifiedName(), imports);
	    for (final MTypeRef intf : type.getInterfaces()) {
		recursiveCleanUpImports(intf, imports);
	    }
	}
    }

    private static final void recursiveCleanUpImports(Class<?> type, TreeSet<String> imports) {
	if (type != null) {
	    MFunctions.cleanUpInnerClassImports(type.getName(), imports);
	    recursiveCleanUpImports(type.getSuperclass(), imports);
	    for (final Class<?> intf : type.getInterfaces()) {
		recursiveCleanUpImports(intf, imports);
	    }
	}
    }

    private static final void recursiveCleanUpImports(MTypeRef ref, TreeSet<String> imports) {
	if (ref instanceof MTypeRefModel) {
	    recursiveCleanUpImports(((MTypeRefModel) ref).getRef(), imports, false);
	} else if (ref instanceof MTypeRefJava) {
	    recursiveCleanUpImports(((MTypeRefJava) ref).getRef(), imports);
	}
    }

    private static void cleanUpInnerClassImports(String typeName, TreeSet<String> imports) {
	for (final String path : new HashSet<String>(imports)) {
	    if (path.startsWith(typeName)) {
		if (path.split("\\.").length == typeName.split("\\.").length + 1) {
		    imports.remove(path);
		}
	    }
	}
    }

    static final String getBrackedGeneric(String generic) {
	return getBrackedGeneric(generic, true);
    }

    static final String getBrackedGeneric(String generic, boolean enforceBrackets) {
	if (generic == null || generic.trim().isEmpty()) {
	    return "";
	}
	generic = generic.trim();
	if (enforceBrackets && !generic.startsWith("<") && !generic.endsWith(">")) {
	    generic = "<".concat(generic);
	    generic = generic.concat(">");
	}
	return generic;
    }

    public static String getComponentTypeName(Class<?> type) {
	return type.getComponentType().isPrimitive() ? null : type.getComponentType().getName();
    }

    static final void addImport(TreeSet<String> imports, Class<?> type, String pckg) {
	if (!isPrimitive(type) || type != null && type.getPackage() != null && type.getPackage().getName().equals(pckg)) {
	    final String i = type.isArray() ? getComponentTypeName(type) : type.getName();
	    if (i != null && !i.trim().isEmpty()) {
		imports.add(i.replace("$", "."));
	    }
	}
    }

    static final void addImport(TreeSet<String> imports, MTypeRef type, String pckg) {
	if (type instanceof MTypeRefJava) {
	    addImport(imports, ((MTypeRefJava) type).getRef(), pckg);
	} else if (type != null) {
	    final String i = type.getImport();
	    if (i != null && !i.trim().isEmpty()) {
		imports.add(i);
	    }
	}
    }

    static final void addImport(TreeSet<String> imports, String imprt) {
	imports.add(imprt.replace("$", "."));
    }

    public static final boolean isUpperCase(String s) {
	return s != null && s.toUpperCase().equals(s);
    }

    public static final boolean isLowerCase(String s) {
	return s != null && s.toLowerCase().equals(s);
    }

    // some_string --> SomeString
    public static final String camelize(String s) {
	return camelize(s, "");
    }

    private static String camelize(String s, String underscoreReplacement) {
	if (s != null && s.length() > 0) {
	    final StringBuffer r = new StringBuffer();
	    boolean nextToUpperCase = true;
	    for (int i = 0; i < s.length(); i++) {
		final String c = s.substring(i, i + 1);
		if (!c.equals("_")) {
		    if (nextToUpperCase) {
			r.append(c.toUpperCase());
			nextToUpperCase = false;
		    } else {
			r.append(c);
		    }
		} else {
		    r.append(underscoreReplacement);
		    nextToUpperCase = true;
		}
	    }
	    return r.toString();
	}
	return s;
    }

    // some_string --> Some String
    public static final String labelize(String s) {
	return camelize(s.toLowerCase(), " ");
    }

    // SomeString --> SOME_STRING
    public static final String constantize(String s) {
	if (s != null && s.length() > 0) {
	    if (!isUpperCase(s)) {
		final StringBuffer r = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
		    final String c = s.substring(i, i + 1);
		    if (isUpperCase(c) && i > 0 && !isNumber(c) && !c.equals("_")) {
			r.append("_");
		    }
		    r.append(c.toUpperCase());
		}
		return r.toString();
	    }
	}
	return s;
    }

    private static boolean isNumber(String c) {
	return "0123456789".contains(c);
    }

    // SomeString --> someString
    public static final String fieldname(String s) {
	if (s != null && s.length() > 1) {
	    return s.substring(0, 1).toLowerCase().concat(s.substring(1));
	}
	return s;
    }

    // SomeString,get --> getSomeString
    public static final String methodName(String s, String prefix) {
	if (s != null && s.length() > 1 && s.substring(1, 2).toUpperCase().equals(s.substring(1, 2))) {
	    return prefix.concat(s.substring(0, 1).toLowerCase().concat(s.substring(1)));
	}
	if (s != null && s.length() > 0) {
	    return prefix.concat(s.substring(0, 1).toUpperCase().concat(s.substring(1)));
	}
	return s;
    }

    public static final String getterMethodName(String s) {
	return methodName(s, "get");
    }

    public static final String getterMethodName(Class<?> type, String s) {
	if (boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type)) {
	    return methodName(s, "is");
	} else {
	    return methodName(s, "get");
	}
    }

    public static final String getterMethodName(MTypeRef type, String s) {
	if (type instanceof MTypeRefJava) {
	    return getterMethodName(((MTypeRefJava) type).getRef(), s);
	}
	return methodName(s, "get");
    }

    public static final String setterMethodName(String s) {
	return methodName(s, "set");
    }

    // singular
    public static final String singular(String s) {
	if (s != null && s.length() > 0) {
	    return s.endsWith("es") ? s.substring(0, s.length() - 2) : s.endsWith("s") ? s.substring(0, s.length() - 1) : s;
	}
	return s;
    }

    // plural
    public static final String plural(String s) {
	if (s != null && s.length() > 0) {
	    if (s.endsWith("es")) {
		return s;// already plural!
	    }
	    if (s.endsWith("ss")) {
		return s.concat("es");
	    }
	    if (s.endsWith("is")) {
		final String base = s.substring(0, s.length() - 2);
		return base.concat("es");
	    }
	    if (s.endsWith("y")) {
		final String base = s.substring(0, s.length() - 1);
		if ("aeiou".contains(String.valueOf(base.charAt(base.length() - 1)).toLowerCase())) {
		    return s.concat("s");
		}
		return base.concat("ies");
	    }
	    return s.endsWith("s") ? s.concat("es") : s.concat("s");
	}
	return s;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static String toGenericString(MHasImports hasImports, Class<?>... types) {
	final StringBuffer sb = new StringBuffer();
	sb.append("<");
	for (final Class<?> type : types) {
	    if (sb.length() > 1) {
		sb.append(", ");
	    }
	    hasImports.addImport(type);
	    sb.append(type.getSimpleName());
	}
	sb.append(">");
	return sb.toString();
    }

    public static String toGenericString(Class<?>... types) {
	final StringBuffer sb = new StringBuffer();
	sb.append("<");
	for (final Class<?> type : types) {
	    if (sb.length() > 1) {
		sb.append(", ");
	    }
	    sb.append(type.getName());
	}
	sb.append(">");
	return sb.toString();
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static boolean hasOneGenericType(Field field) {
	return getGenericTypesCount(field) == 1;
    }

    public static boolean hasTwoGenericTypes(Field field) {
	return getGenericTypesCount(field) == 2;
    }

    public static int getGenericTypesCount(Field field) {
	final Type genericType = field.getGenericType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    return actualTypeArguments.length;
	}
	return 0;
    }

    public static Class<?>[] getGenericTypes(Field field) {
	final Type genericType = field.getGenericType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    final Class<?>[] types = new Class<?>[actualTypeArguments.length];
	    int i = 0;
	    for (final Type actualTypeArgument : actualTypeArguments) {
		types[i] = (Class<?>) actualTypeArgument;
		i = i + 1;
	    }
	    return types;
	}
	return new Class<?>[0];
    }

    public static Class<?> getGenericType(Field field) {
	final Class<?> genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
	return genericType;
    }

    public static Pair<Class<?>, Class<?>> getTwoGenericTypes(Field field) {
	final Class genericType01 = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
	final Class genericType02 = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[1];
	return (Pair<Class<?>, Class<?>>) (Pair<?, ?>) Pair.of(genericType01, genericType02);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static boolean hasOneGenericReturnType(Method method) {
	return getGenericReturnTypesCount(method) == 1;
    }

    public static boolean hasTwoGenericReturnTypes(Method method) {
	return getGenericReturnTypesCount(method) == 2;
    }

    public static int getGenericReturnTypesCount(Method method) {
	final Type genericType = method.getGenericReturnType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    return actualTypeArguments.length;
	}
	return 0;
    }

    public static Class<?>[] getGenericReturnTypes(Method method) {
	final Type genericType = method.getGenericReturnType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    final Class<?>[] types = new Class<?>[actualTypeArguments.length];
	    int i = 0;
	    for (final Type actualTypeArgument : actualTypeArguments) {
		types[i] = (Class<?>) actualTypeArgument;
		i = i + 1;
	    }
	    return types;
	}
	return new Class<?>[0];
    }

    public static Class<?> getGenericReturnType(Method method) {
	final Class<?> genericType = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
	return genericType;
    }

    public static Pair<Class<?>, Class<?>> getTwoGenericReturnTypes(Method method) {
	final Class genericType01 = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
	final Class genericType02 = (Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[1];
	return (Pair<Class<?>, Class<?>>) (Pair<?, ?>) Pair.of(genericType01, genericType02);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static boolean hasOneGenericType(Parameter parameter) {
	return getGenericTypesCount(parameter) == 1;
    }

    public static boolean hasTwoGenericTypes(Parameter parameter) {
	return getGenericTypesCount(parameter) == 2;
    }

    public static int getGenericTypesCount(Parameter parameter) {
	final Type genericType = parameter.getParameterizedType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    return actualTypeArguments.length;
	}
	return 0;
    }

    public static Class<?>[] getGenericTypes(Parameter parameter) {
	final Type genericType = parameter.getParameterizedType();
	if (genericType instanceof ParameterizedType) {
	    final Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
	    final Class<?>[] types = new Class<?>[actualTypeArguments.length];
	    int i = 0;
	    for (final Type actualTypeArgument : actualTypeArguments) {
		types[i] = (Class<?>) actualTypeArgument;
		i = i + 1;
	    }
	    return types;
	}
	return new Class<?>[0];
    }

    public static Class<?> getGenericType(Parameter parameter) {
	if (parameter.getParameterizedType() instanceof ParameterizedType) {
	    final Class genericType = (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
	    return genericType;
	}
	return null;
    }

    public static Pair<Class<?>, Class<?>> getTwoGenericTypes(Parameter parameter) {
	final Class genericType01 = (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[0];
	final Class genericType02 = (Class<?>) ((ParameterizedType) parameter.getParameterizedType()).getActualTypeArguments()[1];
	return (Pair<Class<?>, Class<?>>) (Pair<?, ?>) Pair.of(genericType01, genericType02);
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static void copyFields(MClass type, Collection<Field> fields, BiConsumer<Field, MField> visitor) {
	for (final Field field : fields) {
	    visitor.accept(field, copyField(type, field));
	}
    }

    public static void copyNotStaticFields(MClass type, Class<?> cls, BiConsumer<Field, MField> visitor) {
	for (final Field field : cls.getFields()) {
	    if (!Modifier.isStatic(field.getModifiers())) {
		visitor.accept(field, copyField(type, field));
	    }
	}
    }

    public static void copyNotStaticDeclaredFields(MClass type, Class<?> cls, BiConsumer<Field, MField> visitor) {
	for (final Field field : cls.getDeclaredFields()) {
	    if (!Modifier.isStatic(field.getModifiers())) {
		visitor.accept(field, copyField(type, field));
	    }
	}
    }

    public static void copyStaticFields(MClass type, Class<?> cls, BiConsumer<Field, MField> visitor) {
	for (final Field field : cls.getFields()) {
	    if (Modifier.isStatic(field.getModifiers())) {
		visitor.accept(field, copyField(type, field));
	    }
	}
    }

    public static void copyStaticDeclaredFields(MClass type, Class<?> cls, BiConsumer<Field, MField> visitor) {
	for (final Field field : cls.getDeclaredFields()) {
	    if (Modifier.isStatic(field.getModifiers())) {
		visitor.accept(field, copyField(type, field));
	    }
	}
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static void copyMethods(MClass type, Collection<Method> methods, BiConsumer<Method, MMethod> visitor) {
	for (final Method method : methods) {
	    visitor.accept(method, copyMethod(type, method));
	}
    }

    public static void copyNotStaticMethods(MClass type, Class<?> cls, BiConsumer<Method, MMethod> visitor) {
	for (final Method method : cls.getMethods()) {
	    if (!Modifier.isStatic(method.getModifiers())) {
		visitor.accept(method, copyMethod(type, method));
	    }
	}
    }

    public static void copyNotStaticDeclaredMethods(MClass type, Class<?> cls, BiConsumer<Method, MMethod> visitor) {
	for (final Method method : cls.getDeclaredMethods()) {
	    if (!Modifier.isStatic(method.getModifiers())) {
		visitor.accept(method, copyMethod(type, method));
	    }
	}
    }

    public static void copyStaticMethods(MClass type, Class<?> cls, BiConsumer<Method, MMethod> visitor) {
	for (final Method method : cls.getMethods()) {
	    if (Modifier.isStatic(method.getModifiers())) {
		visitor.accept(method, copyMethod(type, method));
	    }
	}
    }

    public static void copyStaticDeclaredMethods(MClass type, Class<?> cls, BiConsumer<Method, MMethod> visitor) {
	for (final Method method : cls.getDeclaredMethods()) {
	    if (Modifier.isStatic(method.getModifiers())) {
		visitor.accept(method, copyMethod(type, method));
	    }
	}
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static MField copyField(MClass type, Field field) {
	final MField f = type.addField(field.getType(), field.getName());
	decorate(field, f);
	if (getGenericTypesCount(field) > 0) {
	    f.setGeneric(toGenericString(f, getGenericTypes(field)));
	}
	return f;
    }

    public static MField copyField(MEnum type, Field field) {
	final MField f = type.addField(field.getType(), field.getName());
	decorate(field, f);
	if (getGenericTypesCount(field) > 0) {
	    f.setGeneric(toGenericString(f, getGenericTypes(field)));
	}
	return f;
    }

    private static void decorate(Field field, MField f) {
	final int modifiers = field.getModifiers();
	f.setStatic(Modifier.isStatic(modifiers));
	f.setFinal(Modifier.isFinal(modifiers));
	f.makeDefault();
	f.setPublic(Modifier.isPublic(modifiers));
	f.setProtected(Modifier.isProtected(modifiers));
	f.setPrivate(Modifier.isPrivate(modifiers));
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static List<MParameter> extractParameters(Method method, boolean useParanamer) {
	final List<MParameter> list = new ArrayList<MParameter>();
	String[] parameterNames = null;
	if (useParanamer) {
	    final Paranamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	    parameterNames = paranamer.lookupParameterNames(method, false);
	}
	int i = 0;
	for (final Parameter parameter : method.getParameters()) {
	    final String name = useParanamer && parameterNames.length > 0 ? parameterNames[i] : parameter.getName();
	    final MParameter p = new MParameter(parameter.getType(), name);
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    list.add(p);
	    i = i + 1;
	}
	return list;
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static MMethod copyMethod(MClass type, Method method) {
	final MParameter[] parameters = new MParameter[method.getParameterTypes().length];
	int i = 0;
	for (final Parameter parameter : method.getParameters()) {
	    final MParameter p = new MParameter(parameter.getType(), parameter.getName());
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    parameters[i] = p;
	    i = i + 1;
	}
	final MMethod m = type.addMethod(method.getName(), method.getReturnType(), parameters);
	decorate(method, m);
	if (getGenericReturnTypesCount(method) > 0) {
	    m.setGenericReturnType(toGenericString(m, getGenericReturnType(method)));
	}
	return m;
    }

    public static MMethod copyMethod(MInterface type, Method method) {
	final MParameter[] parameters = new MParameter[method.getParameterTypes().length];
	int i = 0;
	for (final Parameter parameter : method.getParameters()) {
	    final MParameter p = new MParameter(parameter.getType(), parameter.getName());
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    parameters[i] = p;
	    i = i + 1;
	}
	final MMethod m = type.addMethod(method.getName(), method.getReturnType(), parameters);
	decorate(method, m);
	if (getGenericReturnTypesCount(method) > 0) {
	    m.setGenericReturnType(toGenericString(m, getGenericReturnType(method)));
	}
	return m;
    }

    public static MMethod copyMethod(MEnum type, Method method) {
	final MParameter[] parameters = new MParameter[method.getParameterTypes().length];
	int i = 0;
	for (final Parameter parameter : method.getParameters()) {
	    final MParameter p = new MParameter(parameter.getType(), parameter.getName());
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    parameters[i] = p;
	    i = i + 1;
	}
	final MMethod m = type.addMethod(method.getName(), method.getReturnType(), parameters);
	decorate(method, m);
	if (getGenericReturnTypesCount(method) > 0) {
	    m.setGenericReturnType(toGenericString(m, getGenericReturnType(method)));
	}
	return m;
    }

    private static void decorate(Method method, MMethod m) {
	final int modifiers = method.getModifiers();
	m.setStatic(Modifier.isStatic(modifiers));
	m.setAbstract(Modifier.isAbstract(modifiers));
	m.makeDefault();
	m.setPublic(Modifier.isPublic(modifiers));
	m.setProtected(Modifier.isProtected(modifiers));
	m.setPrivate(Modifier.isPrivate(modifiers));
	m.setFinal(Modifier.isFinal(modifiers));
	m.setSynchronized(Modifier.isSynchronized(modifiers));
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    public static void copyConstructors(MClass type, Class<?> cls, BiConsumer<Constructor<?>, MConstructor> visitor) {
	copyConstructors(type, Arrays.asList(cls.getConstructors()), visitor);
    }

    public static void copyConstructors(MClass type, Collection<Constructor<?>> contructors, BiConsumer<Constructor<?>, MConstructor> visitor) {
	for (final Constructor<?> constructor : contructors) {
	    visitor.accept(constructor, copyConstructor(type, constructor));
	}
    }

    public static MConstructor copyConstructor(MClass type, Constructor<?> constructor) {
	final MParameter[] parameters = new MParameter[constructor.getParameterTypes().length];
	int i = 0;
	for (final Parameter parameter : constructor.getParameters()) {
	    final MParameter p = new MParameter(parameter.getType(), parameter.getName());
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    parameters[i] = p;
	    i = i + 1;
	}
	final MConstructor c = type.addConstructor(parameters);
	decorate(constructor, c);
	return c;
    }

    public static void copyConstructors(MEnum type, Class<?> cls, BiConsumer<Constructor<?>, MConstructor> visitor) {
	copyConstructors(type, Arrays.asList(cls.getConstructors()), visitor);
    }

    public static void copyConstructors(MEnum type, Collection<Constructor<?>> contructors, BiConsumer<Constructor<?>, MConstructor> visitor) {
	for (final Constructor<?> constructor : contructors) {
	    visitor.accept(constructor, copyConstructor(type, constructor));
	}
    }

    public static MConstructor copyConstructor(MEnum type, Constructor<?> constructor) {
	final MParameter[] parameters = new MParameter[constructor.getParameterTypes().length];
	int i = 0;
	for (final Parameter parameter : constructor.getParameters()) {
	    final MParameter p = new MParameter(parameter.getType(), parameter.getName());
	    if (getGenericTypesCount(parameter) > 0) {
		p.setGeneric(toGenericString(p, getGenericTypes(parameter)));
	    }
	    parameters[i] = p;
	    i = i + 1;
	}
	final MConstructor c = type.addConstructor(parameters);
	decorate(constructor, c);
	return c;
    }

    private static void decorate(Constructor constructor, MConstructor c) {
	final int modifiers = constructor.getModifiers();
	c.makeDefault();
	c.setPublic(Modifier.isPublic(modifiers));
	c.setProtected(Modifier.isProtected(modifiers));
	c.setPrivate(Modifier.isPrivate(modifiers));
    }

    // ----------------------------------------------------------------------------------------------------------------------------------------
    protected MFunctions() {
	super();
    }

    public static boolean isBoolean(Class<?> type) {
	return boolean.class.isAssignableFrom(type) || Boolean.class.isAssignableFrom(type);
    }
}
