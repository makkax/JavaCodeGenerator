package com.cc.jcg;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.cc.jcg.MClass.MClassModifier;
import com.cc.jcg.MMethod.MMethodModifier;

public class MEnum
	extends MConstructable<MEnum>
	implements MCode<MEnum> {

    public enum MEnumModifier implements MModifier {
	PUBLIC, PRIVATE, DEFAULT;
    }

    public final MEnum makePublic() {
	return setModifier(MEnumModifier.PUBLIC);
    }

    public final MEnum makePrivate() {
	return setModifier(MEnumModifier.PRIVATE);
    }

    public final MEnum makeDefault() {
	return setModifier(MEnumModifier.DEFAULT);
    }

    private final MPackage pckg;
    private final String name;
    private final LinkedHashSet<MEnumValue> values = new LinkedHashSet<MEnumValue>();
    private final LinkedHashSet<MTypeRef> implementsTypes = new LinkedHashSet<MTypeRef>();
    private MEnumModifier modifier;
    private MCodeGenerator<MEnum> generator;

    MEnum(MPackage pckg, String name) {
	super();
	this.pckg = pckg;
	this.name = name;
	generator = this;
	modifier = MEnumModifier.PUBLIC;
    }

    @Override
    public final MPackage getPckg() {
	return pckg;
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
    public final String getName() {
	return name;
    }

    @Override
    public Class<?> getJavaType() throws ClassNotFoundException {
	return Class.forName(getQualifiedName());
    }

    public final synchronized MEnumModifier getModifier() {
	return modifier;
    }

    public final synchronized MEnum setModifier(MEnumModifier modifier) {
	this.modifier = modifier;
	return this;
    }

    @Override
    public final String getQualifiedName() {
	return pckg.getName().concat(".").concat(name);
    }

    @Override
    public MField addField(Class<?> type, String name) {
	MField field = super.addField(type, name);
	field.makePrivate();
	field.setFinal(true);
	return field;
    }

    @Override
    public MConstructor addConstructor(Collection<MParameter> parameters) {
	MConstructor constructor = super.addConstructor(parameters);
	constructor.makePrivate();
	return constructor;
    }

    @Override
    public MConstructor addConstructor(Set<MField> fields) {
	MConstructor constructor = super.addConstructor(fields);
	constructor.makePrivate();
	return constructor;
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
    public TreeSet<String> getImports() {
	TreeSet<String> imports = super.getImports();
	for (MTypeRef type : implementsTypes) {
	    addImport(imports, type);
	}
	MFunctions.recursiveCleanUpImports(this, imports, true);
	return imports;
    }

    public final Set<MEnumValue> getValues() {
	return Collections.unmodifiableSet(values);
    }

    public final synchronized MEnum addValue(String value) {
	return addValue(new MEnumValue(value));
    }

    public MEnum addValue(MEnumValue value) {
	values.add(value);
	return this;
    }

    public final MEnum addValue(String value, Object... properties) {
	for (Object property : properties) {
	    if (property instanceof Class) {
		addImport((Class<?>) property);
	    }
	}
	return addValue(new MEnumValue(value, properties));
    }

    public final MEnum addValue(String value, Collection<Object> properties) {
	return addValue(new MEnumValue(value, properties));
    }

    public final MEnum addValues(String... values) {
	return addValues(Arrays.asList(values));
    }

    public final synchronized MEnum addValues(Collection<String> values) {
	for (String value : values) {
	    addValue(value);
	}
	return this;
    }

    public final Set<MTypeRef> getInterfaces() {
	return Collections.unmodifiableSet(implementsTypes);
    }

    public MEnum addInterface(MInterface type) {
	implementsTypes.add(new MTypeRefModel(type));
	return this;
    }

    public MEnum addInterface(Class<?> type) {
	if (!type.isInterface()) {
	    throw new RuntimeException(type + " is not an interface");
	}
	implementsTypes.add(new MTypeRefJava(type));
	return this;
    }

    public final MClass newDispatcher() throws ClassNotFoundException {
	return newDispatcher(pckg);
    }

    public final MClass newDispatcher(MPackage pckg) throws ClassNotFoundException {
	return newDispatcher(pckg, name.concat("Dispatcher"));
    }

    public final MClass newDispatcher(MPackage pckg, String name) throws ClassNotFoundException {
	MClass type = pckg.newClass(name);
	type.setAbstract(true);
	type.setModifier(MClassModifier.PUBLIC);
	type.setGeneric("R");
	type.addMethod("dispatch", "R", new MParameter(this, "value")).setFinal(true).setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock code = element.getCodeBlock(element);
		code.addLine("switch (value) {");
		for (MEnumValue value : values) {
		    code.addLine("    case " + value.getName() + ":");
		    code.addLine("        return " + value.getName() + "();");
		}
		code.addLine("}");
		code.addLine("throw new RuntimeException(\"unexpected value \" + value);");
		return code;
	    }
	});
	for (MEnumValue value : values) {
	    type.addMethod(value.getName(), "R").setAbstract(true).setModifier(MMethodModifier.PROTECTED);
	}
	return type;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("MEnum [name=");
	builder.append(name);
	builder.append(", pckg=");
	builder.append(pckg);
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
	MEnum other = (MEnum) obj;
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
    public MCodeBlock getCodeBlock(MEnum element) {
	new MGuessOverrides(this).call();
	StringBuffer before = new StringBuffer();
	if (!(this instanceof MInnerEnum)) {
	    before.append("package " + pckg.getName() + ";");
	    before.append("\n\n");
	    TreeSet<String> imports = getImports();
	    for (String i : imports) {
		before.append("import " + i + ";\n");
	    }
	    before.append(imports.isEmpty() ? "" : "\n");
	}
	appendAnnotation(before);
	before.append(modifier.equals(MEnumModifier.DEFAULT) ? "" : modifier.name().toLowerCase());
	before.append(onEmptyNoSpace(before) + "enum " + element.getName());
	if (!implementsTypes.isEmpty()) {
	    before.append("\n");
	    before.append(MCodeBlock.tabs(2) + "implements ");
	    AtomicBoolean isFirst = new AtomicBoolean(true);
	    for (MTypeRef type : implementsTypes) {
		before.append(isFirst.getAndSet(false) ? "" : ", ");
		before.append(type.getSimpleName());
	    }
	}
	before.append(" {");
	String after = "}";
	MCodeBlock inner = new MCodeBlock(before, after);
	for (MEnumValue value : values) {
	    if (isLastValue(value)) {
		inner.addLine(value.getValueBuilder() + ";");
	    } else {
		inner.addLine(value.getValueBuilder() + ",");
	    }
	}
	inner.addEmptyLine();
	for (MField field : fields) {
	    inner.addLines(field.getGenerator().getCodeBlock(field).getLines());
	}
	for (MConstructor constructor : constructors) {
	    inner.addEmptyLine();
	    inner.addLines(constructor.getGenerator().getCodeBlock(constructor).getLines());
	}
	for (MMethod method : methods) {
	    inner.addEmptyLine();
	    inner.addLines(method.getGenerator().getCodeBlock(method).getLines());
	}
	return inner;
    }

    private boolean isLastValue(MEnumValue value) {
	AtomicInteger index = new AtomicInteger(0);
	for (MEnumValue v : values) {
	    if (index.incrementAndGet() == values.size()) {
		return value.equals(v);
	    }
	}
	return false;
    }

    @Override
    public final synchronized MEnum setGenerator(MCodeGenerator<MEnum> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public final synchronized MCodeGenerator<MEnum> getGenerator() {
	return generator;
    }
}
