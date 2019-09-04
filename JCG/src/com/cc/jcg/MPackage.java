package com.cc.jcg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cc.jcg.MClass.MClassModifier;
import com.cc.jcg.MConstructor.MConstructorModifier;
import com.cc.jcg.MMethod.MMethodModifier;
import com.cc.jcg.tools.BufferedCopy;
import com.cc.jcg.tools.EncodedFileReader;
import com.cc.jcg.tools.EncodedFileWriter;
import com.cc.jcg.tools.ResourcesAsStream;

public class MPackage
	extends MFunctions
	implements MGenerator {

    public static AtomicBoolean GENERATE_READONLY = new AtomicBoolean(true);
    private final File dir;
    private final String name;
    private final LinkedHashSet<MInterface> interfaces = new LinkedHashSet<MInterface>();
    private final LinkedHashSet<MAnnotation> annotations = new LinkedHashSet<MAnnotation>();
    private final LinkedHashSet<MClass> classes = new LinkedHashSet<MClass>();
    private final LinkedHashSet<MEnum> enums = new LinkedHashSet<MEnum>();
    private final LinkedHashSet<MJavaFile> javaFiles = new LinkedHashSet<MJavaFile>();
    private boolean overrideTemplates;
    private boolean failOnRepeatedDefinitions;
    private final File templatesDir;
    private final MBundle bundle;

    MPackage(MBundle bundle, File dir, File templatesDir, String name) {
	super();
	this.bundle = bundle;
	this.dir = dir;
	this.templatesDir = templatesDir;
	this.name = name;
	overrideTemplates = false;
	failOnRepeatedDefinitions = true;
    }

    public final MBundle getBundle() {
	return bundle;
    }

    public final File getDir() {
	return dir;
    }

    public final File getTemplatesDir() {
	return templatesDir;
    }

    public final String getName() {
	return name;
    }

    public final Set<MInterface> getInterfaces() {
	return Collections.unmodifiableSet(interfaces);
    }

    public final Set<MClass> getClasses() {
	return Collections.unmodifiableSet(classes);
    }

    public final Set<MEnum> getEnums() {
	return Collections.unmodifiableSet(enums);
    }

    public final Set<MJavaFile> getJavaFiles() {
	return Collections.unmodifiableSet(javaFiles);
    }

    public final MInterface getInterfaceByName(String name) {
	for (final MInterface type : interfaces) {
	    if (type.getName().equals(name)) {
		return type;
	    }
	}
	return null;
    }

    public final MClass getClassByName(String name) {
	for (final MClass type : classes) {
	    if (type.getName().equals(name)) {
		return type;
	    }
	}
	return null;
    }

    public final MEnum getEnumByName(String name) {
	for (final MEnum type : enums) {
	    if (type.getName().equals(name)) {
		return type;
	    }
	}
	return null;
    }

    public final MInterface removeInterfaceByName(String name) {
	final MInterface type = getInterfaceByName(name);
	if (type != null) {
	    interfaces.remove(type);
	}
	return type;
    }

    public final MClass removeClassByName(String name) {
	final MClass type = getClassByName(name);
	if (type != null) {
	    classes.remove(type);
	}
	return type;
    }

    public final MEnum removeEnumByName(String name) {
	final MEnum type = getEnumByName(name);
	if (type != null) {
	    enums.remove(type);
	}
	return type;
    }

    public final Collection<MType> getAllTypes() {
	final LinkedHashSet<MType> all = new LinkedHashSet<MType>();
	all.addAll(getInterfaces());
	all.addAll(getClasses());
	all.addAll(getEnums());
	return all;
    }

    public final void setFailOnRepeatedDefinitions(boolean boo) {
	failOnRepeatedDefinitions = boo;
    }

    public final MPackage newSubPackage(String name) {
	return bundle.newPackage(getName().concat(".").concat(name));
    }

    public MInterface newInterface(String name) {
	final MInterface type = new MInterface(this, name);
	if (interfaces.contains(type) && failOnRepeatedDefinitions) {
	    throw new RuntimeException(type + " already defined in " + toString() + " (failOnRepeatedDefinitions = true)");
	}
	interfaces.add(type);
	return type;
    }

    public MInterface newInterface(MInterface supertype, String name) {
	final MInterface type = newInterface(name);
	type.addInterface(supertype);
	return type;
    }

    public MAnnotation newAnnotationRuntime(String name, ElementType annType) {
	return newAnnotation(name, annType, RetentionPolicy.RUNTIME);
    }

    public MAnnotation newAnnotationSource(String name, ElementType annType) {
	return newAnnotation(name, annType, RetentionPolicy.SOURCE);
    }

    public MAnnotation newAnnotation(String name, ElementType annType, RetentionPolicy annRetention) {
	final MAnnotation type = new MAnnotation(this, name);
	type.setType(annType);
	type.setRetention(annRetention);
	if (annotations.contains(type) && failOnRepeatedDefinitions) {
	    throw new RuntimeException(type + " already defined in " + toString() + " (failOnRepeatedDefinitions = true)");
	}
	annotations.add(type);
	return type;
    }

    public MClass newClass(String name) {
	final MClass type = new MClass(this, name);
	if (classes.contains(type) && failOnRepeatedDefinitions) {
	    throw new RuntimeException(type + " already defined in " + toString() + " (failOnRepeatedDefinitions = true)");
	}
	classes.add(type);
	return type;
    }

    public MClass newSubclass(MClass supertype, String name) {
	return supertype.newSubclass(name);
    }

    public MClass newClass(MInterface intf, String name) {
	final MClass type = newClass(name);
	type.addInterface(intf);
	return type;
    }

    public MEnum newEnum(String name) {
	final MEnum type = new MEnum(this, name);
	if (enums.contains(type) && failOnRepeatedDefinitions) {
	    throw new RuntimeException(type + " already defined in " + toString() + " (failOnRepeatedDefinitions = true)");
	}
	enums.add(type);
	return type;
    }

    public MJavaFile addJavaFile(File file) {
	final MJavaFile type = new MJavaFile(this, file, javaFiles);
	javaFiles.add(type);
	return type;
    }

    public MJavaFile addJavaFile(File file, String name) {
	final MJavaFile type = new MJavaFile(this, file, name, javaFiles);
	javaFiles.add(type);
	return type;
    }

    public List<MJavaFile> addJavaFiles(File dir) {
	final List<MJavaFile> types = new ArrayList<MJavaFile>();
	for (final File file : dir.listFiles()) {
	    if (file.getName().endsWith(".java")) {
		final MJavaFile type = new MJavaFile(this, file, javaFiles);
		javaFiles.add(type);
	    }
	}
	return types;
    }

    public MJavaFile addJavaFileAsStream(Class<?> typeWithJavaCode) throws IOException {
	final String name = typeWithJavaCode.getSimpleName();
	final MJavaFile jf = addJavaFile(ResourcesAsStream.getResourceAsStream(typeWithJavaCode, name + ".java"), typeWithJavaCode.getSimpleName());
	return jf.setJavaType(typeWithJavaCode);
    }

    public MJavaFile addJavaFile(InputStream stream, String name) throws IOException {
	final File file = File.createTempFile("jcg-", ".java");
	file.delete();
	final FileOutputStream out = new FileOutputStream(file);
	try {
	    int read = 0;
	    final byte[] bytes = new byte[1024];
	    while ((read = stream.read(bytes)) != -1) {
		out.write(bytes, 0, read);
	    }
	} finally {
	    out.flush();
	    out.close();
	}
	try {
	    final MJavaFile type = new MJavaFile(this, file, name, javaFiles);
	    javaFiles.add(type);
	    return type;
	} finally {
	    if (file.exists()) {
		file.delete();
	    }
	}
    }

    public final MClass newDelegatorClass(MType wrapped) throws ClassNotFoundException {
	final String name = wrapped.getSrcFile().getName().replace(".java", "").concat("Delegate");
	return newWrapperClass(name, wrapped, true);
    }

    public final MClass newWrapperClass(MType wrapped, boolean delegatePublicMethods) throws ClassNotFoundException {
	final String name = wrapped.getSrcFile().getName().replace(".java", "").concat("Wrapper");
	return newWrapperClass(name, wrapped, delegatePublicMethods);
    }

    @SuppressWarnings("rawtypes")
    public MClass newWrapperClass(String name, MType wrapped, boolean delegatePublicMethods) throws ClassNotFoundException {
	final MClass type = newClass(name);
	type.addField(wrapped, "wrapped").setFinal(true).addGetterMethod().setFinal(true).setSynchronized(false);
	type.addConstructor(new MParameter(wrapped, "wrapped")).setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		final MCodeBlock code = element.getCodeBlock(element);
		code.addLine("super();");
		code.addLine("this.wrapped = wrapped;");
		return code;
	    }
	}).setModifier(MConstructorModifier.PUBLIC);
	if (delegatePublicMethods) {
	    for (final MMethod m : wrapped.getMethods()) {
		if (m.getModifier().equals(MMethodModifier.PUBLIC)) {
		    final MParameter[] parameters = new MParameter[m.getParameters().size()];
		    int i = 0;
		    for (final MParameter pt : m.getParameters()) {
			parameters[i] = pt;
			i = i + 1;
		    }
		    final MTypeRef returnType = m.getReturnType();
		    type.addMethod(m.getName(), returnType, parameters).setGenerator(new MCodeGenerator<MMethod>() {

			@Override
			public MCodeBlock getCodeBlock(MMethod element) {
			    final MCodeBlock code = element.getCodeBlock(element);
			    code.addLine((returnType.isVoid() ? "" : "return ") + "wrapped." + m.getName() + "(" + element.getParameterNames() + ");");
			    return code;
			}
		    }).setFinal(false).setModifier(MMethodModifier.PUBLIC);
		}
	    }
	}
	return type;
    }

    public final MClass newBean(String name, MParameter... fields) throws ClassNotFoundException {
	return newBean(name, Arrays.asList(fields));
    }

    public MClass newBean(String name, Collection<MParameter> fields) throws ClassNotFoundException {
	final MClass type = newClass(name);
	type.addConstructor().makePublic().addContentRow("super();");
	for (final MParameter par : fields) {
	    final MField field = type.addField(par.getType(), par.getName()).setGeneric(par.getGenericType());
	    for (final String imp : par.getImports()) {
		field.addImport(imp);
	    }
	    field.addAccessorMethods();
	}
	return type;
    }

    public MClass newTypeSafeEnum(String name, MEnum mEum) {
	return newTypeSafeEnum(name, mEum.getValues());
    }

    public MClass newTypeSafeEnum(String name, Collection<?> values) {
	final MClass type = newClass(name).setFinal(true).makePublic();
	type.addConstructor(new MParameter(String.class, "name")).setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		final MCodeBlock code = element.getCodeBlock(element);
		code.addLine("super();");
		code.addLine("this.name = name;");
		return code;
	    }
	}).makePrivate();
	type.addField(String.class, "name").makePrivateFinal();
	type.addMethod("name", String.class).makePublic().setBlockContent("return name;");
	for (final Object value : values) {
	    type.addStaticField(type, value.toString()).makePublic().setFinal(true).setValue("new " + type.getName() + "(\"" + value.toString() + "\");");
	}
	return type;
    }

    public final MClass newEnumDispatcher(Class<? extends Enum<?>> eType, String name) throws ClassNotFoundException {
	MClass type = newClass(name);
	type.setAbstract(true);
	type.setModifier(MClassModifier.PUBLIC);
	type.setGeneric("R");
	type.addMethod("dispatch", "R", new MParameter(eType, "value")).setFinal(true).setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock code = element.getCodeBlock(element);
		code.addLine("switch (value) {");
		for (Enum<?> value : eType.getEnumConstants()) {
		    code.addLine("    case " + value.name() + ":");
		    code.addLine("        return " + value.name() + "();");
		}
		code.addLine("}");
		code.addLine("throw new RuntimeException(\"unexpected value \" + value);");
		return code;
	    }
	});
	for (Enum<?> value : eType.getEnumConstants()) {
	    type.addMethod(value.name(), "R").setAbstract(true).setModifier(MMethodModifier.PROTECTED);
	}
	return type;
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MPackage [dir=");
	builder.append(dir);
	builder.append(", name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (dir == null ? 0 : dir.hashCode());
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
	final MPackage other = (MPackage) obj;
	if (dir == null) {
	    if (other.dir != null) {
		return false;
	    }
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

    synchronized void setOverrideTemplates(boolean overrideTemplates) {
	this.overrideTemplates = overrideTemplates;
    }

    @Override
    public void generateCode(boolean clean) throws Exception {
	final Set<File> templates = new HashSet<File>();
	try {
	    Bundle.set(bundle);
	    for (final MJavaFile type : javaFiles) {
		if (type.isTemplate()) {
		    templates.add(type.getSrcFile());
		}
	    }
	    for (final MAnnotation type : annotations) {
		if (type.isTemplate()) {
		    templates.add(type.getSrcFile());
		}
	    }
	    for (final MInterface type : interfaces) {
		if (type.isTemplate()) {
		    templates.add(type.getSrcFile());
		}
	    }
	    for (final MClass type : classes) {
		if (type.isTemplate()) {
		    templates.add(type.getSrcFile());
		}
	    }
	    for (final MEnum type : enums) {
		if (type.isTemplate()) {
		    templates.add(type.getSrcFile());
		}
	    }
	    if (!getDir().exists()) {
		getDir().mkdirs();
	    }
	    if (!getTemplatesDir().exists()) {
		getTemplatesDir().mkdirs();
	    }
	    if (clean) {
		final List<File> all = new ArrayList<File>();
		all.addAll(Arrays.asList(getDir().listFiles()));
		all.addAll(Arrays.asList(getTemplatesDir().listFiles()));
		for (final File file : all) {
		    final boolean isTemplate = templates.contains(file);
		    if (file.isFile() && !isTemplate || isTemplate && overrideTemplates) {
			if (!isLocked(file)) {
			    if (isTemplate) {
				System.out.println("RENAMING  " + file.getAbsolutePath());
				final File last = new File(file.getParentFile(), file.getName().concat(".old"));
				if (last.exists()) {
				    last.delete();
				}
				file.renameTo(last);
			    } else if (isGenerated(file)) {
				System.out.println("DELETING  " + file.getAbsolutePath());
				file.delete();
			    }
			} else {
			    System.out.println("LOCKED " + file.getAbsolutePath() + " is not deleted");
			}
		    }
		}
	    }
	    for (final MJavaFile type : javaFiles) {
		final File output = type.getSrcFile();
		if (overrideTemplates || !type.isTemplate() || type.isTemplate() && !output.exists()) {
		    generateCode(type, output);
		}
	    }
	    for (final MAnnotation type : annotations) {
		final File output = type.getSrcFile();
		if (overrideTemplates || !type.isTemplate() || type.isTemplate() && !output.exists()) {
		    generateCode(type, output);
		}
	    }
	    for (final MInterface type : interfaces) {
		final File output = type.getSrcFile();
		if (overrideTemplates || !type.isTemplate() || type.isTemplate() && !output.exists()) {
		    generateCode(type, output);
		}
	    }
	    for (final MClass type : classes) {
		final File output = type.getSrcFile();
		if (overrideTemplates || !type.isTemplate() || type.isTemplate() && !output.exists()) {
		    generateCode(type, output);
		}
	    }
	    for (final MEnum type : enums) {
		final File output = type.getSrcFile();
		if (overrideTemplates || !type.isTemplate() || type.isTemplate() && !output.exists()) {
		    generateCode(type, output);
		}
	    }
	} finally {
	    Bundle.remove();
	}
    }

    private static boolean isGenerated(File file) {
	final AtomicBoolean generated = new AtomicBoolean(false);
	if (file.exists()) {
	    try {
		new EncodedFileReader(file) {

		    @Override
		    protected void onLine(String line) throws Exception {
			final String cleaned = line.replace(" ", "").replace("\t", "");
			if (cleaned.startsWith("@" + MGenerated.class.getSimpleName()) || cleaned.startsWith("@" + MGenerated.class.getName())) {
			    generated.set(true);
			    stop.set(true);
			}
		    }
		}.call();
	    } catch (final Exception e) {
		System.out.println("WARNING: " + e.getMessage());
	    }
	}
	return generated.get();
    }

    private static boolean isLocked(File file) {
	final AtomicBoolean locked = new AtomicBoolean(false);
	if (file.exists()) {
	    try {
		new EncodedFileReader(file) {

		    @Override
		    protected void onLine(String line) throws Exception {
			final String cleaned = line.replace(" ", "").replace("\t", "");
			if (cleaned.startsWith("@" + MLocked.class.getSimpleName()) || cleaned.startsWith("@" + MLocked.class.getName())) {
			    locked.set(true);
			    stop.set(true);
			}
		    }
		}.call();
	    } catch (final Exception e) {
		System.out.println("WARNING: " + e.getMessage());
	    }
	}
	return locked.get();
    }

    public void addResource(File file) throws Exception {
	if (file.exists()) {
	    dir.mkdirs();
	    final File target = new File(dir, file.getName());
	    if (target.exists()) {
		target.delete();
	    }
	    // Files.copy(file.toPath(), target.toPath());
	    new BufferedCopy().copy(file, target);
	}
    }

    private static ThreadLocal<MBundle> Bundle = new ThreadLocal<MBundle>();

    public static MBundle getGeneratingBundle() {
	return Bundle.get();
    }

    private void generateCode(final MCode type, File output) throws Exception {
	if (!isLocked(output) && !(type instanceof MExcludable && !((MExcludable) type).isToGenerate())) {
	    System.out.println("-----------------------------------------------------------------------------------------------------------------------");
	    System.out.println("GENERATING " + type.toString());
	    System.out.println("OUTPUT     " + output.getAbsolutePath());
	    System.out.println("-----------------------------------------------------------------------------------------------------------------------");
	    output.setWritable(true);
	    new EncodedFileWriter(output) {

		@Override
		protected void writing() throws Exception {
		    final MCodeGenerator generator = type.getGenerator();
		    if (bundle.getGeneratedCodeListener() != null) {
			if (type instanceof MJavaFile) {
			    bundle.getGeneratedCodeListener().onNew((MJavaFile) type);
			} else if (type instanceof MAnnotation) {
			    bundle.getGeneratedCodeListener().onNew((MAnnotation) type);
			} else if (type instanceof MInterface) {
			    bundle.getGeneratedCodeListener().onNew((MInterface) type);
			} else if (type instanceof MClass) {
			    bundle.getGeneratedCodeListener().onNew((MClass) type);
			} else if (type instanceof MEnum) {
			    bundle.getGeneratedCodeListener().onNew((MEnum) type);
			}
		    }
		    for (final String line : generator.getCodeBlock(type).getLines()) {
			if (bundle.getGeneratedCodeListener() != null) {
			    if (type instanceof MJavaFile) {
				bundle.getGeneratedCodeListener().on((MJavaFile) type, line);
			    } else if (type instanceof MAnnotation) {
				bundle.getGeneratedCodeListener().on((MAnnotation) type, line);
			    } else if (type instanceof MInterface) {
				bundle.getGeneratedCodeListener().on((MInterface) type, line);
			    } else if (type instanceof MClass) {
				bundle.getGeneratedCodeListener().on((MClass) type, line);
			    } else if (type instanceof MEnum) {
				bundle.getGeneratedCodeListener().on((MEnum) type, line);
			    }
			}
			writeLine(line);
		    }
		}
	    }.call();
	    if (type instanceof MType && ((MType) type).isTemplate()) {
		output.setWritable(true);
	    } else {
		output.setWritable(!GENERATE_READONLY.get());
	    }
	} else {
	    System.out.println("LOCKED " + output.getAbsolutePath() + " is not overwritten");
	}
    }

    public final boolean isEnsureCollectionGenerics() {
	return bundle.isEnsureCollectionGenerics();
    }

    public final boolean isEnsureMapGenerics() {
	return bundle.isEnsureMapGenerics();
    }
}
