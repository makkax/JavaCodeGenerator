package com.cc.jcg;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MMethod
	extends MAnnotated<MMethod>
	implements MCode<MMethod> {

    public enum MMethodModifier implements MModifier {
	PUBLIC, PROTECTED, PRIVATE, DEFAULT;
    }

    public final MMethod makePublic() {
	return setModifier(MMethodModifier.PUBLIC);
    }

    public final MMethod makeProtected() {
	return setModifier(MMethodModifier.PROTECTED);
    }

    public final MMethod makePrivate() {
	return setModifier(MMethodModifier.PRIVATE);
    }

    public final MMethod makeDefault() {
	return setModifier(MMethodModifier.DEFAULT);
    }

    public final MMethod setPublic(boolean boo) {
	if (boo) {
	    return makePublic();
	}
	return this;
    }

    public final MMethod setProtected(boolean boo) {
	if (boo) {
	    return makeProtected();
	}
	return this;
    }

    public final MMethod setPrivate(boolean boo) {
	if (boo) {
	    return makePrivate();
	}
	return this;
    }

    public final MMethod setDefault(boolean boo) {
	if (boo) {
	    return makeDefault();
	}
	return this;
    }

    private final MType container;
    private final String name;
    private final MTypeRef returnType;
    private final LinkedHashSet<MParameter> parameters = new LinkedHashSet<MParameter>();
    private final LinkedHashSet<Class<? extends Throwable>> exceptions = new LinkedHashSet<Class<? extends Throwable>>();
    private MMethodModifier modifier;
    private boolean isSynchronized;
    private boolean isFinal;
    private boolean isStatic;
    private boolean isAbstract;
    private String genericReturnType;
    private MCodeGenerator<MMethod> generator;
    private final LinkedList<String> contentRows;
    private String comment = null;

    MMethod(MType container, String name, Class<?> returnType, MParameter... parameters) {
	this(container, name, new MTypeRefJava(returnType), parameters);
    }

    MMethod(MType container, String name, MType returnType, MParameter... parameters) {
	this(container, name, new MTypeRefModel(returnType), parameters);
    }

    MMethod(MType container, String name, Class<?> returnType, String genericReturnType, MParameter... parameters) {
	this(container, name, new MTypeRefJava(returnType), genericReturnType, parameters);
    }

    MMethod(MType container, String name, MType returnType, String genericReturnType, MParameter... parameters) {
	this(container, name, new MTypeRefModel(returnType), genericReturnType, parameters);
    }

    MMethod(MType container, String name, String genericReturnType, MParameter... parameters) {
	this(container, name, new MTypeRefGeneric(genericReturnType), parameters);
    }

    MMethod(MType container, String name, MTypeRef returnType, MParameter... parameters) {
	this(container, name, returnType, "", parameters);
    }

    MMethod(MType container, String name, MTypeRef returnType, String genericReturnType, MParameter... parameters) {
	super();
	this.container = container;
	this.name = name;
	this.returnType = returnType;
	setGenericReturnType(genericReturnType);
	modifier = MMethodModifier.PUBLIC;
	isFinal = false;
	isAbstract = false;
	this.parameters.addAll(Arrays.asList(parameters));
	generator = this;
	isStatic = false;
	contentRows = new LinkedList<String>();
    }

    public final MType getOwner() {
	return container;
    }

    public final String getName() {
	return name;
    }

    public final MTypeRef getReturnType() {
	return returnType;
    }

    public final boolean isVoid() {
	return returnType == null || (void.class.equals(returnType.getRef()) || Void.class.equals(returnType.getRef())) && genericReturnType.isEmpty();
    }

    public final synchronized String getGenericReturnType() {
	if ((genericReturnType == null || genericReturnType.trim().isEmpty()) && returnType instanceof MTypeRefJava) {
	    MTypeRefJava casted = (MTypeRefJava) returnType;
	    if (Collection.class.isAssignableFrom(casted.getRef()) && container.getPckg().isEnsureCollectionGenerics()) {
		return "<?>";
	    }
	    if (Map.class.isAssignableFrom(casted.getRef()) && container.getPckg().isEnsureMapGenerics()) {
		return "<?, ?>";
	    }
	}
	return genericReturnType;
    }

    public final synchronized MMethod setGenericReturnType(String generic) {
	genericReturnType = MFunctions.getBrackedGeneric(generic, false);
	return this;
    }

    public final synchronized MMethodModifier getModifier() {
	return modifier;
    }

    public final synchronized MMethod setModifier(MMethodModifier modifier) {
	this.modifier = modifier;
	return this;
    }

    public final synchronized boolean isSynchronized() {
	return isSynchronized;
    }

    public final synchronized MMethod setSynchronized(boolean isSynchronized) {
	this.isSynchronized = isSynchronized;
	return this;
    }

    public final synchronized boolean isFinal() {
	return isFinal;
    }

    public final synchronized MMethod setFinal(boolean isFinal) {
	this.isFinal = isFinal;
	return this;
    }

    public final synchronized boolean isStatic() {
	return isStatic;
    }

    public final synchronized MMethod setStatic(boolean isStatic) {
	this.isStatic = isStatic;
	return this;
    }

    public final synchronized boolean isAbstract() {
	return isAbstract;
    }

    public final synchronized boolean isProtected() {
	return modifier.equals(MMethodModifier.PROTECTED);
    }

    public final synchronized MMethod setAbstract(boolean isAbstract) {
	this.isAbstract = isAbstract;
	if (isAbstract) {
	    generator = this;
	    setFinal(false);
	    setSynchronized(false);
	    if (container instanceof MClass) {
		((MClass) container).setAbstract(true);
		((MClass) container).setFinal(false);
	    }
	}
	return this;
    }

    public final Set<MParameter> getParameters() {
	return Collections.unmodifiableSet(parameters);
    }

    public final String getParameterNames() {
	final StringBuffer sb = new StringBuffer();
	for (final MParameter p : parameters) {
	    sb.append(p.getName());
	    sb.append(", ");
	}
	if (sb.length() > 0) {
	    sb.delete(sb.length() - 2, sb.length());
	}
	return sb.toString();
    }

    public final Set<Class<? extends Throwable>> getExceptions() {
	return Collections.unmodifiableSet(exceptions);
    }

    public final MMethod throwsException(Class<? extends Throwable> e) {
	exceptions.add(e);
	return this;
    }

    public final MMethod throwsExceptions(Class<? extends Throwable>... es) {
	for (Class<? extends Throwable> e : es) {
	    exceptions.add(e);
	}
	return this;
    }

    public final MMethod returnNull() {
	setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		final MCodeBlock block = element.getCodeBlock(element);
		block.addLine("return null;");
		return block;
	    }
	});
	return this;
    }

    public MMethod returnSuper() {
	if (!isVoid()) {
	    setReturnValue("return super." + getName() + "(" + getParameterNames() + ");");
	} else {
	    setBlockContent("super." + getName() + "(" + getParameterNames() + ");");
	}
	return this;
    }

    public final MMethod setReturnValue(StringBuffer s) {
	return setReturnValue(s.toString());
    }

    public final MMethod setReturnValue(final String s) {
	setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		final MCodeBlock block = element.getCodeBlock(element);
		if (!s.trim().startsWith("return")) {
		    block.addLine("return " + s + (s.trim().endsWith(";") ? "" : ";"));
		} else {
		    block.addLine(s.trim() + (s.trim().endsWith(";") ? "" : ";"));
		}
		return block;
	    }
	});
	return this;
    }

    public final synchronized LinkedList<String> getContentRows() {
	return contentRows;
    }

    public final synchronized LinkedList<String> addContentRow(String line) {
	contentRows.add(line);
	return contentRows;
    }

    public final synchronized LinkedList<String> addContentRow(String line, Class... extraImports) {
	for (final Class type : extraImports) {
	    if (getOwner() instanceof MClass) {
		((MClass) getOwner()).addExtraImport(type);
	    }
	    if (getOwner() instanceof MEnum) {
		((MEnum) getOwner()).addExtraImport(type);
	    }
	    if (getOwner() instanceof MInterface) {
		((MInterface) getOwner()).addExtraImport(type);
	    }
	}
	return addContentRow(line);
    }

    public final synchronized LinkedList<String> addContentRow(String line, MTypeRef... extraImports) {
	for (final MTypeRef type : extraImports) {
	    if (getOwner() instanceof MClass) {
		((MClass) getOwner()).addExtraImport(type);
	    }
	    if (getOwner() instanceof MEnum) {
		((MEnum) getOwner()).addExtraImport(type);
	    }
	    if (getOwner() instanceof MInterface) {
		((MInterface) getOwner()).addExtraImport(type);
	    }
	}
	return addContentRow(line);
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

    public final synchronized String getComment() {
	return comment;
    }

    public final synchronized void setComment(String comment) {
	this.comment = comment != null && !comment.startsWith("//") ? "// " + comment.trim() : comment;// comment
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MMethod [container=");
	builder.append(container);
	builder.append(", returnType=");
	builder.append(returnType.getSimpleName());
	builder.append(", name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (parameters == null ? 0 : parameters.hashCode());
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
	final MMethod other = (MMethod) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (parameters == null) {
	    if (other.parameters != null) {
		return false;
	    }
	} else if (!parameters.equals(other.parameters)) {
	    return false;
	}
	return true;
    }

    private String onEmptyNoSpace(StringBuffer sb) {
	return sb.length() == 0 || sb.charAt(sb.length() - 1) == '\n' ? "" : " ";
    }

    private String genericSymbolDefinition;

    public synchronized final String getGenericSymbolDefinition() {
	return genericSymbolDefinition;
    }

    public synchronized final MMethod setGenericSymbolDefinition(String genericSymbolDefinition) {
	this.genericSymbolDefinition = genericSymbolDefinition;
	return this;
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MMethod element) {
	final StringBuffer before = new StringBuffer();
	if (comment != null) {
	    before.append(comment + "\n");
	}
	appendAnnotation(before);
	before.append(modifier.equals(MMethodModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	before.append(isStatic ? onEmptyNoSpace(before) + "static" : "");
	before.append(isFinal ? onEmptyNoSpace(before) + "final" : "");
	before.append(isAbstract ? onEmptyNoSpace(before) + "abstract" : "");
	before.append(isSynchronized ? onEmptyNoSpace(before) + "synchronized" : "");
	before.append(genericSymbolDefinition != null ? onEmptyNoSpace(before) + genericSymbolDefinition.trim() : "");
	if (isVoid()) {
	    before.append(onEmptyNoSpace(before) + "void");
	} else {
	    String generic = getGenericReturnType();
	    if (returnType != null) {
		before.append(onEmptyNoSpace(before) + returnType.getSimpleName().concat(generic));
	    } else if (!generic.isEmpty()) {
		before.append(onEmptyNoSpace(before) + generic);
	    }
	}
	before.append(" ");
	before.append(name);
	before.append("(");
	final AtomicBoolean isFirst = new AtomicBoolean(true);
	for (final MParameter parameter : parameters) {
	    before.append(isFirst.getAndSet(false) ? "" : ", ");
	    before.append(parameter.getCodeBlock(parameter).getLines().getFirst());
	}
	before.append(")");
	if (!exceptions.isEmpty()) {
	    before.append(" throws " + getExceptionsNames());
	}
	before.append(isAbstract || this instanceof MAbstractMethod ? ";" : " {");
	final String after = isAbstract || this instanceof MAbstractMethod ? null : "}";
	return new MCodeBlock(before, after);
    }

    private String getExceptionsNames() {
	final StringBuffer sb = new StringBuffer();
	for (final Class<? extends Throwable> e : exceptions) {
	    sb.append(e.getSimpleName());
	    sb.append(", ");
	}
	if (sb.length() > 0) {
	    sb.delete(sb.length() - 2, sb.length());
	}
	return sb.toString();
    }

    @Override
    public synchronized MMethod setGenerator(MCodeGenerator<MMethod> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MMethod> getGenerator() {
	if (!contentRows.isEmpty()) {
	    setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock block = element.getCodeBlock(element);
		    for (final String line : contentRows) {
			block.addLine(line);
		    }
		    return block;
		}
	    });
	}
	return generator;
    }

    public final MMethod setBlockContent(StringBuffer sb) {
	return setBlockContent(sb.toString());
    }

    public MMethod setBlockContent(final String line) {
	if (line != null && !line.replace("\n", "").replace(" ", "").isEmpty()) {
	    return setGenerator(new MCodeGenerator<MMethod>() {

		@Override
		public MCodeBlock getCodeBlock(MMethod element) {
		    final MCodeBlock block = element.getCodeBlock(element);
		    block.addLine(line);
		    return block;
		}
	    });
	}
	return this;
    }

    public MMethod overrides() {
	for (final Annotation a : annotations) {
	    if (a.annotationType().equals(Override.class)) {
		return this;
	    }
	}
	addAnnotation(new Override() {

	    @Override
	    public Class<? extends Annotation> annotationType() {
		return Override.class;
	    }
	});
	return this;
    }

    public MMethod notOverriding() {
	for (final Annotation annotation : new ArrayList<Annotation>(annotations)) {
	    if (annotation.annotationType().equals(Override.class)) {
		annotations.remove(annotation);
		break;
	    }
	}
	return this;
    }
}
