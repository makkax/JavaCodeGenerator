package com.cc.jcg;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.cc.jcg.MMethod.MMethodModifier;

public class MAnnotation
	extends MAnnotated<MAnnotation>
	implements MType, MCode<MAnnotation> {

    public enum MInterfaceModifier implements MModifier {
	PUBLIC, DEFAULT;
    }

    public final MAnnotation makePublic() {
	return setModifier(MInterfaceModifier.PUBLIC);
    }

    public final MAnnotation makeDefault() {
	return setModifier(MInterfaceModifier.DEFAULT);
    }

    private final MPackage pckg;
    private final String name;
    private final LinkedHashSet<MAbstractMethod> methods = new LinkedHashSet<MAbstractMethod>();
    private final TreeSet<String> extraImports = new TreeSet<String>();
    private MInterfaceModifier modifier;
    private MCodeGenerator<MAnnotation> generator;
    // @Target(ElementType.TYPE)
    // @Retention(RetentionPolicy.RUNTIME)
    private ElementType type = ElementType.TYPE;
    private RetentionPolicy retention = RetentionPolicy.RUNTIME;

    public final synchronized ElementType getType() {
	return type;
    }

    public final synchronized void setType(ElementType type) {
	this.type = type;
    }

    public final synchronized RetentionPolicy getRetention() {
	return retention;
    }

    public final synchronized void setRetention(RetentionPolicy retention) {
	this.retention = retention;
    }

    MAnnotation(MPackage pckg, String name) {
	super();
	modifier = MInterfaceModifier.PUBLIC;
	this.pckg = pckg;
	this.name = name;
	generator = this;
    }

    @Override
    public final MPackage getPckg() {
	return pckg;
    }

    private boolean template;

    @Override
    public final synchronized MAnnotation setTemplate(boolean boo) {
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

    public final synchronized MAnnotation setModifier(MInterfaceModifier type) {
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

    @Override
    public final Set<MAbstractMethod> getMethods() {
	return Collections.unmodifiableSet(methods);
    }

    public final void removeMethod(MAbstractMethod method) {
	methods.remove(method);
    }

    public MAbstractMethod addMethod(String name, MTypeRef returnType, MParameter... parameters) {
	MAbstractMethod method = new MAbstractMethod(this, name, returnType, parameters);
	method.setModifier(MMethodModifier.DEFAULT);
	methods.add(method);
	return method;
    }

    public final MAbstractMethod addMethod(String name, String genericReturnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefGeneric(genericReturnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefJava(returnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, MType returnType, MParameter... parameters) {
	return addMethod(name, new MTypeRefModel(returnType), parameters);
    }

    public final MAbstractMethod addMethod(String name, Class<?> returnType, String genericReturnType, MParameter... parameters) {
	MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    public final MAbstractMethod addMethod(String name, MType returnType, String genericReturnType, MParameter... parameters) {
	MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    public final MAbstractMethod addMethod(String name, MTypeRef returnType, String genericReturnType, MParameter... parameters) {
	MAbstractMethod method = addMethod(name, returnType, parameters);
	method.setGenericReturnType(genericReturnType);
	return method;
    }

    @Override
    public final TreeSet<String> getImports() {
	TreeSet<String> imports = super.getImports();
	for (MMethod method : methods) {
	    addImport(imports, method.getReturnType());
	    for (Class<? extends Throwable> e : method.getExceptions()) {
		addImport(imports, e);
	    }
	    for (String i : method.getImports()) {
		imports.add(i);
	    }
	    for (MParameter parameter : method.getParameters()) {
		addImport(imports, parameter.getType());
		addImport(imports, parameter.getImports());
	    }
	}
	imports.addAll(extraImports);
	MFunctions.recursiveCleanUpImports(this, imports, true);
	return imports;
    }

    @Override
    public final synchronized MAnnotation addExtraImport(Class<?> type) {
	addImport(extraImports, type);
	return this;
    }

    public final synchronized MAnnotation addExtraImport(MType type) {
	addImport(extraImports, new MTypeRefModel(type));
	return this;
    }

    @Override
    public final synchronized MAnnotation addExtraImport(MTypeRef type) {
	addImport(extraImports, type);
	return this;
    }

    @Override
    public final synchronized MAnnotation addExtraImport(String imprt) {
	addImport(extraImports, imprt);
	return this;
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
	StringBuilder builder = new StringBuilder();
	builder.append("MAnnotation [name=");
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
	MAnnotation other = (MAnnotation) obj;
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
    protected void appendAnnotation(StringBuffer sb) {
	super.appendAnnotation(sb);
	// @Target(ElementType.TYPE)
	// @Retention(RetentionPolicy.RUNTIME)
	sb.append("@Target(ElementType." + type.name() + ")\n");
	sb.append("@Retention(RetentionPolicy." + retention.name() + ")\n");
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MAnnotation element) {
	// import java.lang.annotation.ElementType;
	// import java.lang.annotation.Retention;
	// import java.lang.annotation.RetentionPolicy;
	// import java.lang.annotation.Target;
	addExtraImport(java.lang.annotation.ElementType.class);
	addExtraImport(java.lang.annotation.Retention.class);
	addExtraImport(java.lang.annotation.RetentionPolicy.class);
	addExtraImport(java.lang.annotation.Target.class);
	//
	new MGuessOverrides(this).call();
	StringBuffer before = new StringBuffer();
	before.append("package " + pckg.getName() + ";");
	before.append("\n\n");
	TreeSet<String> imports = getImports();
	for (String i : imports) {
	    before.append("import " + i + ";\n");
	}
	before.append(imports.isEmpty() ? "" : "\n");
	appendAnnotation(before);
	before.append(modifier.equals(MInterfaceModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	afterModifier(before);
	before.append(onEmptyNoSpace(before) + "@interface");
	before.append(onEmptyNoSpace(before) + element.getName());
	before.append(" {");
	String after = "}";
	MCodeBlock inner = new MCodeBlock(before, after);
	for (MMethod method : methods) {
	    method.setAbstract(false);
	    inner.addEmptyLine();
	    inner.addLines(method.getGenerator().getCodeBlock(method).getLines());
	    method.setAbstract(true);
	}
	return inner;
    }

    public final boolean isSerializable() {
	return false;
    }

    protected void afterModifier(StringBuffer before) {
    }

    @Override
    public final synchronized MAnnotation setGenerator(MCodeGenerator<MAnnotation> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MAnnotation> getGenerator() {
	return generator;
    }
}
