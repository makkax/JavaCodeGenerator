package com.cc.jcg;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class MConstructor
	extends MAnnotated<MConstructor>
	implements MCode<MConstructor> {

    public enum MConstructorModifier implements MModifier {
	PUBLIC, PROTECTED, PRIVATE, DEFAULT;
    }

    public final MConstructor makePublic() {
	return setModifier(MConstructorModifier.PUBLIC);
    }

    public final MConstructor makeProtected() {
	return setModifier(MConstructorModifier.PROTECTED);
    }

    public final MConstructor makePrivate() {
	return setModifier(MConstructorModifier.PRIVATE);
    }

    public final MConstructor makeDefault() {
	return setModifier(MConstructorModifier.DEFAULT);
    }

    public final MConstructor setPublic(boolean boo) {
	if (boo) {
	    return makePublic();
	}
	return this;
    }

    public final MConstructor setProtected(boolean boo) {
	if (boo) {
	    return makeProtected();
	}
	return this;
    }

    public final MConstructor setPrivate(boolean boo) {
	if (boo) {
	    return makePrivate();
	}
	return this;
    }

    public final MConstructor setDefault(boolean boo) {
	if (boo) {
	    return makeDefault();
	}
	return this;
    }

    private final MType container;
    private final LinkedHashSet<MParameter> parameters = new LinkedHashSet<MParameter>();
    private final LinkedHashSet<Class<? extends Throwable>> exceptions = new LinkedHashSet<Class<? extends Throwable>>();
    private MConstructorModifier modifier;
    private MCodeGenerator<MConstructor> generator;
    private final LinkedList<String> contentRows;

    MConstructor(MType container, MParameter... parameters) {
	super();
	this.container = container;
	this.parameters.addAll(Arrays.asList(parameters));
	modifier = MConstructorModifier.PUBLIC;
	generator = this;
	contentRows = new LinkedList<String>();
	if (container instanceof MInterface) {
	    throw new RuntimeException("invaolid usage: interfaces have no constructors!");
	}
    }

    public final <T extends MType> T getContainer() {
	return (T) container;
    }

    public final synchronized MConstructorModifier getModifier() {
	return modifier;
    }

    public final synchronized MConstructor setModifier(MConstructorModifier modifier) {
	this.modifier = modifier;
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

    public final MConstructor throwsException(Class<? extends Throwable> e) {
	exceptions.add(e);
	return this;
    }

    @Override
    public final String getPackageName() {
	return container.getPackageName();
    }

    @Override
    public TreeSet<String> getImports() {
	final TreeSet<String> all = super.getImports();
	for (final MParameter par : parameters) {
	    all.addAll(par.getImports());
	}
	return all;
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
	    if (getContainer() instanceof MClass) {
		((MClass) getContainer()).addExtraImport(type);
	    }
	    if (getContainer() instanceof MEnum) {
		((MEnum) getContainer()).addExtraImport(type);
	    }
	}
	return addContentRow(line);
    }

    public final synchronized LinkedList<String> addContentRow(String line, MTypeRef... extraImports) {
	for (final MTypeRef type : extraImports) {
	    if (getContainer() instanceof MClass) {
		((MClass) getContainer()).addExtraImport(type);
	    }
	    if (getContainer() instanceof MEnum) {
		((MEnum) getContainer()).addExtraImport(type);
	    }
	}
	return addContentRow(line);
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
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MConstructor [type=");
	builder.append(container);
	builder.append(", parameters=");
	builder.append(parameters);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (parameters == null ? 0 : parameters.hashCode());
	result = prime * result + (container == null ? 0 : container.hashCode());
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
	final MConstructor other = (MConstructor) obj;
	if (parameters == null) {
	    if (other.parameters != null) {
		return false;
	    }
	} else if (!parameters.equals(other.parameters)) {
	    return false;
	}
	if (container == null) {
	    if (other.container != null) {
		return false;
	    }
	} else if (!container.equals(other.container)) {
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

    public synchronized final MConstructor setGenericSymbolDefinition(String genericSymbolDefinition) {
	this.genericSymbolDefinition = genericSymbolDefinition;
	return this;
    }

    @Override
    public final synchronized MCodeBlock getCodeBlock(MConstructor element) {
	final StringBuffer before = new StringBuffer();
	appendAnnotation(before);
	before.append(modifier.equals(MConstructorModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	before.append(genericSymbolDefinition != null ? onEmptyNoSpace(before) + genericSymbolDefinition.trim() : "");
	before.append(onEmptyNoSpace(before) + element.getContainer().getName());
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
	before.append(" {");
	final String after = "}";
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

    public final MConstructor setBlockContent(final StringBuffer line) {
	return setBlockContent(line.toString());
    }

    public final MConstructor setBlockContent(final String line) {
	setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		final MCodeBlock block = element.getCodeBlock(element);
		block.addLine(line);
		return block;
	    }
	});
	return this;
    }

    @Override
    public final synchronized MConstructor setGenerator(MCodeGenerator<MConstructor> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MConstructor> getGenerator() {
	if (!contentRows.isEmpty()) {
	    setGenerator(new MCodeGenerator<MConstructor>() {

		@Override
		public MCodeBlock getCodeBlock(MConstructor element) {
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
}
