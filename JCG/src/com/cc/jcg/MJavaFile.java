package com.cc.jcg;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.cc.jcg.tools.EncodedFileReader;

public final class MJavaFile
	implements MType, MCode<MJavaFile>, MHasImports {

    private final MPackage pckg;
    private final String filePath;
    private final LinkedList<String> lines;
    private final String originalName;
    private final String name;
    private String originalPackage;
    private final Collection<MJavaFile> allJavaFiles;
    private final TreeSet<String> extraImports = new TreeSet<String>();
    private final ConcurrentMap<String, String> replacements = new ConcurrentHashMap<String, String>();
    private MCodeGenerator<MJavaFile> generator;
    private Class<?> javaType;

    MJavaFile(MPackage pckg, File file, Collection<MJavaFile> allJavaFiles) {
	this(pckg, file, file.getName().substring(0, file.getName().length() - ".java".length()), allJavaFiles);
    }

    MJavaFile(MPackage pckg, File file, String name, Collection<MJavaFile> allJavaFiles) {
	super();
	lines = new LinkedList<String>();
	this.pckg = pckg;
	filePath = file.getAbsolutePath();
	this.name = name.endsWith(".java") ? name.substring(0, file.getName().length() - ".java".length()) : name;
	originalName = file.getName().endsWith(".java") ? file.getName().substring(0, file.getName().length() - ".java".length()) : name;
	generator = this;
	this.allJavaFiles = allJavaFiles;
	javaType = null;
	try {
	    new EncodedFileReader(file) {

		@Override
		protected void onLine(String line) throws Exception {
		    lines.add(line);
		    if (line.startsWith("package ")) {
			String pckg = line.substring("package ".length());
			pckg = pckg.trim();
			pckg = pckg.substring(0, pckg.length() - 1);
			setOriginalPackage(pckg);
		    }
		}
	    }.call();
	} catch (final Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public final MPackage getPckg() {
	return pckg;
    }

    public String getFilePath() {
	return filePath;
    }

    public void setOriginalPackage(String originalPackage) {
	this.originalPackage = originalPackage;
    }

    public String getOriginalPackage() {
	return originalPackage;
    }

    public ConcurrentMap<String, String> getReplacements() {
	return replacements;
    }

    public String getOriginalName() {
	return originalName;
    }

    @Override
    public String getName() {
	return name;
    }

    @Override
    public String getQualifiedName() {
	return pckg.getName() + "." + name;
    }

    @Override
    public String getPackageName() {
	return pckg.getName();
    }

    @Override
    public File getSrcFile() {
	return new File(pckg.getDir(), name.concat(".java"));
    }

    protected void addImport(TreeSet<String> imports, Class<?> type) {
	MFunctions.addImport(imports, type, getPackageName());
    }

    protected void addImport(TreeSet<String> imports, MTypeRef type) {
	MFunctions.addImport(imports, type, getPackageName());
    }

    protected void addImport(TreeSet<String> imports, String fullname) {
	MFunctions.addImport(imports, fullname);
    }

    @Override
    public TreeSet<String> getImports() {
	return extraImports;
    }

    @Override
    public Set<? extends MMethod> getMethods() {
	return Collections.EMPTY_SET;
    }

    private boolean template;

    @Override
    public synchronized MJavaFile setTemplate(boolean boo) {
	template = boo;
	return this;
    }

    @Override
    public synchronized boolean isTemplate() {
	return template;
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
    public MJavaFile addExtraImport(Class<?> type) {
	addImport(extraImports, type);
	return this;
    }

    @Override
    public final void addImport(MType type) {
	addImport(new MTypeRefModel(type));
    }

    @Override
    public MJavaFile addExtraImport(MTypeRef ref) {
	addImport(extraImports, ref);
	return this;
    }

    @Override
    public MJavaFile addExtraImport(String fullname) {
	addImport(extraImports, fullname);
	return this;
    }

    @Override
    public MJavaFile setGenerator(MCodeGenerator<MJavaFile> generator) {
	this.generator = generator;
	return this;
    }

    @Override
    public synchronized MCodeGenerator<MJavaFile> getGenerator() {
	return generator;
    }

    @Override
    public Class<?> getJavaType() throws ClassNotFoundException {
	return javaType != null ? javaType : Class.forName(getQualifiedName());
    }

    public MJavaFile setJavaType(Class<?> javaType) {
	this.javaType = javaType;
	return this;
    }

    @Override
    public MCodeBlock getCodeBlock(MJavaFile element) {
	final MCodeBlock inner = new MCodeBlock(true);
	try {
	    for (String line : lines) {
		if (line.startsWith("package ")) {// replace package
		    inner.addLine("package " + pckg.getName() + ";");
		    if (!extraImports.isEmpty()) {
			inner.addEmptyLine();
		    }
		    for (final String imp : extraImports) {
			inner.addLine("import " + imp + ";");
		    }
		} else if (line.startsWith("import ")) {
		    for (final MJavaFile jf : allJavaFiles) {
			if (!jf.equals(this)) {
			    if (line.startsWith("import " + jf.getOriginalPackage() + "." + jf.getOriginalName())) {
				line = "import " + jf.getQualifiedName() + ";";
				break;
			    }
			}
		    }
		    inner.addLine(line);
		} else {
		    for (final String key : replacements.keySet()) {
			line = line.replace(key, replacements.get(key));
		    }
		    line = replaceOriginalNames(line);
		    inner.addLine(line);
		}
	    }
	} catch (final Exception e) {
	    throw new RuntimeException(e.getMessage());
	}
	return inner;
    }

    private String replaceOriginalNames(String line) {
	for (final MJavaFile jf : allJavaFiles) {
	    line = line.replace(" " + jf.getOriginalName(), " " + jf.getName());
	    line = line.replace(jf.getOriginalName() + " ", jf.getName() + " ");
	    line = line.replace("\t" + jf.getOriginalName(), "\t" + jf.getName());
	    line = line.replace(jf.getOriginalName() + "\t", jf.getName() + "\t");
	    line = line.replace("(" + jf.getOriginalName() + ")", "(" + jf.getName() + ")");
	    line = line.replace(jf.getOriginalName() + ".class", jf.getName() + ".class");
	}
	return line;
    }

    @Override
    public String toString() {
	final StringBuilder builder = new StringBuilder();
	builder.append("MJavaFile [name=");
	builder.append(getQualifiedName());
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
	final MJavaFile other = (MJavaFile) obj;
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
}
