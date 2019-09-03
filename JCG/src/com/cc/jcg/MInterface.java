package com.cc.jcg;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cc.jcg.MMethod.MMethodModifier;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

public class MInterface
	extends MAnnotated<MInterface>
	implements MType, MCode<MInterface>, MExcludable {

    private boolean generate = true;

    @Override
    public final void doNotGenerate() {
	generate = false;
    }

    @Override
    public boolean isToGenerate() {
	return generate;
    }

    public enum MInterfaceModifier implements MModifier {
	PUBLIC, DEFAULT;
    }

    public final MInterface makePublic() {
	return setModifier(MInterfaceModifier.PUBLIC);
    }

    public final MInterface makeDefault() {
	return setModifier(MInterfaceModifier.DEFAULT);
    }

    private final MPackage pckg;
    private final String name;
    private final LinkedHashSet<MAbstractMethod> methods = new LinkedHashSet<MAbstractMethod>();
    private final LinkedHashSet<MTypeRef> extendsTypes = new LinkedHashSet<MTypeRef>();
    private final Map<MTypeRef, String> interfaceGenerics = new HashMap<MTypeRef, String>();
    private final TreeSet<String> extraImports = new TreeSet<String>();
    private MInterfaceModifier modifier;
    private String generic;
    private MCodeGenerator<MInterface> generator;
    private final LinkedHashSet<MInnerInterface> innerInterfaces = new LinkedHashSet<MInnerInterface>();

    MInterface(MPackage pckg, String name) {
	super();
	modifier = MInterfaceModifier.PUBLIC;
	this.pckg = pckg;
	this.name = name;
	generator = this;
	generic = "";
    }

    @Override
    public final MPackage getPckg() {
	return pckg;
    }

    private boolean template;

    @Override
    public final synchronized MInterface setTemplate(boolean boo) {
	template = boo;
	if (template) {
	    addAnnotation(new MTemplate() {

		@Override
		public Class<? extends Annotation> annotationType() {
		    return MTemplate.class;
		}
	    });
	}
	return this;
    }

    @Override
    public final synchronized boolean isTemplate() {
	return template;
    }

    @Override
    public final File getSrcFile() {
	if (isTemplate()) {
	    return new File(pckg.getTemplatesDir(), name.concat(".java"));
	}
	return new File(pckg.getDir(), name.concat(".java"));
    }

    @Override
    public final String getPackageName() {
	return pckg.getName();
    }

    @Override
    public Class<?> getJavaType() throws ClassNotFoundException {
	return Class.forName(getQualifiedName());
    }

    public final synchronized MInterfaceModifier getModifier() {
	return modifier;
    }

    public final synchronized MInterface setModifier(MInterfaceModifier type) {
	modifier = type;
	return this;
    }

    @Override
    public final String getName() {
	return name;
    }

    @Override
    public final String getQualifiedName() {
	return pckg.getName().concat(".").concat(name);
    }

    public final synchronized String getGeneric() {
	return generic;
    }

    public final synchronized void setGeneric(String generic) {
	this.generic = MFunctions.getBrackedGeneric(generic);
    }

    @Override
    public final Set<MAbstractMethod> getMethods() {
	return Collections.unmodifiableSet(methods);
    }

    public final void removeMethod(MAbstractMethod method) {
	methods.remove(method);
    }

    public MAbstractMethod addMethod(MMethod method) {
	final MAbstractMethod copy = addMethod(method.getName(), method.getReturnType(), method.getParameters().toArray(new MParameter[method.getParameters().size()]));
	if (method.getGenericReturnType() != null) {
	    copy.setGenericReturnType(method.getGenericReturnType());
	}
	decorate(method, copy);
	return copy;
    }

    private void decorate(MMethod method, MMethod m) {
	m.setStatic(method.isStatic());
	m.makeDefault();
    }

    public MAbstractMethod addMethod(Method m) {
	final List<MParameter> parameters = new ArrayList<MParameter>();
	final String[] parameterNames = new CachingParanamer(new BytecodeReadingParanamer()).lookupParameterNames(m, false);
	int i = 0;
	for (final Class<?> pt : m.getParameterTypes()) {
	    final String name = parameterNames.length == 0 ? "par" + i : parameterNames[i];
	    parameters.add(new MParameter(pt, name));// Generic!?
	    i++;
	}
	final MAbstractMethod copy = addMethod(m.getName(), m.getReturnType(), parameters.toArray(new MParameter[parameters.size()]));
	// if (m.getGenericReturnType() != null) {
	// copy.setGenericReturnType(m.getGenericReturnType());
	// }
	return copy;
    }

    public MAbstractMethod addMethod(Method m, List<String> parameterNames) {
	final List<MParameter> parameters = new ArrayList<MParameter>();
	int i = 0;
	for (final Class<?> pt : m.getParameterTypes()) {
	    final String name = parameterNames.get(i);
	    parameters.add(new MParameter(pt, name));// Generic!?
	    i++;
	}
	final MAbstractMethod copy = addMethod(m.getName(), m.getReturnType(), parameters.toArray(new MParameter[parameters.size()]));
	// if (m.getGenericReturnType() != null) {
	// copy.setGenericReturnType(m.getGenericReturnType());
	// }
	return copy;
    }

    public MAbstractMethod addMethod(String name, MTypeRef returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters.toArray(new MParameter[parameters.size()]));
    }

    public MAbstractMethod addMethod(String name, MTypeRef returnType, MParameter... parameters) {
	final MAbstractMethod method = new MAbstractMethod(this, name, returnType, parameters);
	method.setModifier(MMethodModifier.DEFAULT);
	methods.add(method);
	return method;
    }

    public final MAbstractMethod addMethod(String name, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, genericReturnType, parameters.toArray(new MParameter[parameters.size()]));
    }

    public final MAbstractMethod addMethod(String name, String genericReturnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefGeneric(genericReturnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters.toArray(new MParameter[parameters.size()]));
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefJava(returnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, MType returnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, parameters.toArray(new MParameter[parameters.size()]));
    }

    public final MAbstractMethod addMethod(String name, MType returnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefModel(returnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, String genericReturnType, Collection<MParameter> parameters) {
	return addMethod(name, returnType, genericReturnType, parameters.toArray(new MParameter[parameters.size()]));
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, String genericReturnType, MParameter... parameters) {
	final MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    public final MAbstractMethod addMethod(String name, MType returnType, String genericReturnType, MParameter... parameters) {
	final MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    public final MAbstractMethod addMethod(String name, MTypeRef returnType, String genericReturnType, MParameter... parameters) {
	final MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    public MInterface newExtension(String name) {
	final MInterface type = pckg.newInterface(name);
	type.addInterface(this);
	return type;
    }

    public MClass newImplementation(String name) {
	final MClass type = pckg.newClass(name);
	type.addInterface(this);
	return type;
    }

    @Override
    public final TreeSet<String> getImports() {
	final TreeSet<String> imports = super.getImports();
	for (final MMethod method : methods) {
	    addImport(imports, method.getReturnType());
	    for (final Class<? extends Throwable> e : method.getExceptions()) {
		addImport(imports, e);
	    }
	    for (final String i : method.getImports()) {
		imports.add(i);
	    }
	    for (final MParameter parameter : method.getParameters()) {
		addImport(imports, parameter.getType());
		addImport(imports, parameter.getImports());
	    }
	}
	for (final MTypeRef type : extendsTypes) {
	    addImport(imports, type);
	}
	for (final MInnerInterface innerIntf : innerInterfaces) {
	    for (final String i : innerIntf.getImports()) {
		imports.add(i);
	    }
	}
	imports.addAll(extraImports);
	MFunctions.recursiveCleanUpImports(this, imports, true);
	return imports;
    }

    @Override
    public final synchronized MInterface addExtraImport(Class<?> type) {
	addImport(extraImports, type);
	return this;
    }

    public final synchronized MInterface addExtraImport(MType type) {
	addImport(extraImports, new MTypeRefModel(type));
	return this;
    }

    @Override
    public final synchronized MInterface addExtraImport(MTypeRef type) {
	addImport(extraImports, type);
	return this;
    }

    @Override
    public final synchronized MInterface addExtraImport(String imprt) {
	addImport(extraImports, imprt);
	return this;
    }

    public final Set<MTypeRef> getInterfaces() {
	return Collections.unmodifiableSet(extendsTypes);
    }

    public MInterface addInterface(MInterface type) {
	extendsTypes.add(new MTypeRefModel(type));
	return this;
    }

    public MInterface addInterface(MJavaFile type) {
	extendsTypes.add(new MTypeRefModel(type));
	return this;
    }

    public final MInterface addInterfaces(List<MInterface> types) {
	for (final MInterface type : types) {
	    addInterface(type);
	}
	return this;
    }

    public final MInterface addInterface(MInterface type, String generic) {
	addInterface(type);
	interfaceGenerics.put(new MTypeRefModel(type), generic);
	return this;
    }

    public final MInterface addInterface(MJavaFile type, String generic) {
	addInterface(type);
	interfaceGenerics.put(new MTypeRefModel(type), generic);
	return this;
    }

    public final MInterface addInterfaces(MInterface... types) {
	return addInterfaces(Arrays.asList(types));
    }

    public MInterface addInterface(Class<?> type) {
	if (!type.isInterface()) {
	    throw new RuntimeException(type + " is not an interface");
	}
	extendsTypes.add(new MTypeRefJava(type));
	return this;
    }

    public final MInterface addInterface(Class<?> type, Class<?> generic) {
	addImport(generic);
	return addInterface(type, generic.getSimpleName());
    }

    public final MInterface addInterface(Class<?> type, String generic) {
	addInterface(type);
	interfaceGenerics.put(new MTypeRefJava(type), generic);
	return this;
    }

    public MInterface addInterfaces(Collection<Class<?>> intfs) {
	for (final Class<?> type : intfs) {
	    addInterface(type);
	}
	return this;
    }

    public MInterface addInterfaces(Class<?>... intfs) {
	return addInterfaces(Arrays.asList(intfs));
    }

    public MInterface newInnerInterfaces(String name) {
	final MInnerInterface type = new MInnerInterface(pckg, name);
	innerInterfaces.add(type);
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
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MInterface [name=");
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
	final MInterface other = (MInterface) obj;
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

    protected static String onEmptyNoSpace(StringBuffer sb) {
	return sb.length() == 0 || sb.charAt(sb.length() - 1) == '\n' ? "" : " ";
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MInterface element) {
	new MGuessOverrides(this).call();
	final StringBuffer before = new StringBuffer();
	if (!(this instanceof MInnerInterface)) {
	    before.append("package " + pckg.getName() + ";");
	    before.append("\n\n");
	    final TreeSet<String> imports = getImports();
	    for (final String i : imports) {
		before.append("import " + i + ";\n");
	    }
	    before.append(imports.isEmpty() ? "" : "\n");
	}
	appendAnnotation(before);
	before.append(modifier.equals(MInterfaceModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	afterModifier(before);
	before.append(onEmptyNoSpace(before) + "interface");
	before.append(onEmptyNoSpace(before) + element.getName());
	if (generic != null && generic.length() > 2) {
	    before.append(generic);
	}
	if (!extendsTypes.isEmpty()) {
	    before.append("\n");
	    before.append(MCodeBlock.tabs(2) + "extends ");
	    final AtomicBoolean isFirst = new AtomicBoolean(true);
	    for (final MTypeRef type : extendsTypes) {
		before.append(isFirst.getAndSet(false) ? "" : ", ");
		before.append(type.getSimpleName().concat(MFunctions.getBrackedGeneric(interfaceGenerics.get(type))));
	    }
	}
	before.append(" {");
	final String after = "}";
	final MCodeBlock inner = new MCodeBlock(before, after);
	for (final MInnerInterface innerIntf : innerInterfaces) {
	    inner.addEmptyLine();
	    inner.addLines(innerIntf.getGenerator().getCodeBlock(innerIntf).getLines());
	}
	for (final MMethod method : methods) {
	    method.setAbstract(false);
	    inner.addEmptyLine();
	    inner.addLines(method.getGenerator().getCodeBlock(method).getLines());
	    method.setAbstract(true);
	}
	return inner;
    }

    public final boolean isSerializable() {
	boolean r = false;
	for (final MTypeRef type : extendsTypes) {
	    r = r || type.isSerializable();
	}
	return r;
    }

    protected void afterModifier(StringBuffer before) {
    }

    @Override
    public final synchronized MInterface setGenerator(MCodeGenerator<MInterface> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MInterface> getGenerator() {
	return generator;
    }
}
