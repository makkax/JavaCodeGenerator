package com.cc.jcg;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.cc.jcg.tools.CopyReplacing;
import com.cc.jcg.tools.DirectoryVisitor;

public class MBundle
	extends MFunctions
	implements MGenerator {

    public static final String VERSION = "1.0";
    public static final AtomicBoolean EXCLUDE_GENERATED_ANNOTATION = new AtomicBoolean(true);
    public static final AtomicBoolean GENERATE_READONLY = MPackage.GENERATE_READONLY;
    public static final AtomicBoolean DO_NOT_GENERATE_EMPTY_PACKAGES = new AtomicBoolean(false);
    private boolean showProgressbar = false;
    private final File srcDir;
    private final File templatesSrcDir;
    private final String name;
    private final LinkedHashSet<MPackage> packages = new LinkedHashSet<MPackage>();
    private final Set<MBundle> bundles = Collections.synchronizedSet(new HashSet<MBundle>());
    private boolean overrideTemplates;
    private MGeneratedCodeListener generatedCodeListener;
    private boolean ensureCollectionGenerics;
    private boolean ensureMapGenerics;
    private boolean doNotGenerateEmptyPackages = DO_NOT_GENERATE_EMPTY_PACKAGES.get();

    public MBundle(String dirPath, String name) throws Exception {
	this(new File(dirPath), new File(dirPath), name);
    }

    public MBundle(File dir, String name) throws Exception {
	this(dir, dir, name);
    }

    public MBundle(File dir) throws Exception {
	this(dir, "MBundle");
    }

    public MBundle(String dirPath, String templatesDir, String name) throws Exception {
	this(new File(dirPath), new File(templatesDir), name);
    }

    public MBundle(File dir, File templatesDir, String name) throws Exception {
	super();
	ensureCollectionGenerics = false;
	ensureMapGenerics = false;
	srcDir = dir;
	templatesSrcDir = templatesDir;
	this.name = name;
	overrideTemplates = false;
	generatedCodeListener = new MGeneratedCodeListener() {

	    @Override
	    public void on(MEnum type, String line) {
		System.out.println(type + ": " + line);
	    }

	    @Override
	    public void on(MClass type, String line) {
		System.out.println(type + ": " + line);
	    }

	    @Override
	    public void on(MInterface type, String line) {
		System.out.println(type + ": " + line);
	    }

	    @Override
	    public void on(MAnnotation type, String line) {
		System.out.println(type + ": " + line);
	    }

	    @Override
	    public void on(MJavaFile type, String line) {
		System.out.println(type + ": " + line);
	    }

	    @Override
	    public void onNew(MAnnotation type) {
		System.out.println(type);
	    }

	    @Override
	    public void onNew(MInterface type) {
		System.out.println(type);
	    }

	    @Override
	    public void onNew(MClass type) {
		System.out.println(type);
	    }

	    @Override
	    public void onNew(MEnum type) {
		System.out.println(type);
	    }

	    @Override
	    public void onNew(MJavaFile type) {
		System.out.println(type);
	    }
	};
	initialize();
    }

    public final synchronized boolean isDoNotGenerateEmptyPackages() {
	return doNotGenerateEmptyPackages;
    }

    public final synchronized void setDoNotGenerateEmptyPackages(boolean doNotGenerateEmptyPackages) {
	this.doNotGenerateEmptyPackages = doNotGenerateEmptyPackages;
    }

    public final synchronized void setEnsureGenerics(boolean ensureGenerics) {
	setEnsureCollectionGenerics(ensureGenerics);
	setEnsureMapGenerics(ensureGenerics);
    }

    public final synchronized boolean isEnsureCollectionGenerics() {
	return ensureCollectionGenerics;
    }

    public final synchronized void setEnsureCollectionGenerics(boolean ensureCollectionGenerics) {
	this.ensureCollectionGenerics = ensureCollectionGenerics;
    }

    public final synchronized boolean isEnsureMapGenerics() {
	return ensureMapGenerics;
    }

    public final synchronized void setEnsureMapGenerics(boolean ensureMapGenerics) {
	this.ensureMapGenerics = ensureMapGenerics;
    }

    public final synchronized MBundle setShowProgressbar(boolean showProgressbar) {
	this.showProgressbar = showProgressbar;
	return this;
    }

    protected void initialize() throws Exception {
    }

    public synchronized void setOverrideTemplates(boolean overrideTemplates) {
	this.overrideTemplates = overrideTemplates;
    }

    public final File getSrcDir() {
	return srcDir;
    }

    public final File getTemplatesSrcDir() {
	return templatesSrcDir;
    }

    public final String getName() {
	return name;
    }

    public final Set<MPackage> getPackages() {
	return Collections.unmodifiableSet(packages);
    }

    public final MPackage getPackageByName(String name) {
	for (MPackage pckg : packages) {
	    if (pckg.getName().equals(name)) {
		return pckg;
	    }
	}
	return null;
    }

    public final MInterface getInterfaceByName(String name) {
	for (MPackage pckg : packages) {
	    for (MInterface e : pckg.getInterfaces()) {
		if (e.getName().equals(name)) {
		    return e;
		}
	    }
	}
	return null;
    }

    public final MClass getClassByName(String name) {
	for (MPackage pckg : packages) {
	    for (MClass e : pckg.getClasses()) {
		if (e.getName().equals(name)) {
		    return e;
		}
	    }
	}
	return null;
    }

    public final MEnum getEnumByName(String name) {
	for (MPackage pckg : packages) {
	    for (MEnum e : pckg.getEnums()) {
		if (e.getName().equals(name)) {
		    return e;
		}
	    }
	}
	return null;
    }

    public final void addSubBundle(MBundle bundle) {
	bundles.add(bundle);
    }

    public final Set<MBundle> getSubBundles() {
	return bundles;
    }

    public MPackage newPackage(Package jpckg) {
	return newPackage(jpckg.getName());
    }

    public MPackage newPackage(String name) {
	File dir = new File(srcDir, name.replace(".", File.separator));
	File templatesDir = new File(templatesSrcDir, name.replace(".", File.separator));
	MPackage pckg = new MPackage(this, dir, templatesDir, name);
	for (MPackage p : packages) {
	    if (p.equals(pckg)) {
		return p;
	    }
	}
	packages.add(pckg);
	return pckg;
    }

    public void correctPackages() throws Exception {
	correctPackages(null);
    }

    public void correctPackages(final String pckgRoot) throws Exception {
	final Map<String, String> replacements = new HashMap<String, String>();
	new DirectoryVisitor(getSrcDir()) {

	    @Override
	    protected void onDirectory(File dir) throws Exception {
		traverseDirectory(dir);
	    }

	    @Override
	    protected void onFile(File file) throws Exception {
		if (file.getName().endsWith(".java")) {
		    correctPackage(file);
		}
	    }

	    private void correctPackage(final File file) throws Exception {
		File patched = new File(file.getPath().concat(".patched"));
		try {
		    if (patched.exists()) {
			patched.delete();
		    }
		    final String relPath = file.getParentFile().getAbsolutePath().substring(srcDir.getAbsolutePath().length() + 1);
		    final String pckg = relPath.replace("\\", ".").replace("/", "");
		    if (pckgRoot == null || pckg.startsWith(pckgRoot)) {
			final AtomicBoolean changed = new AtomicBoolean(false);
			new CopyReplacing(file, patched, Collections.EMPTY_MAP) {

			    private String lineReplacement;

			    @Override
			    protected synchronized String getLineReplacement() {
				return lineReplacement;
			    }

			    private synchronized void setLineReplacement(String lineReplacement) {
				this.lineReplacement = lineReplacement;
			    }

			    @Override
			    protected boolean doWriteLine(String line) {
				setLineReplacement(null);
				if (line.startsWith("package ")) {
				    String pckgLine = "package " + pckg + ";";
				    if (!line.equals(pckgLine)) {
					System.out.println(file.getPath() + ": replacing '" + line + "' with '" + pckgLine + "'");
					replacements.put(extractPackage(line), extractPackage(pckgLine));
					changed.set(true);
					setLineReplacement(pckgLine);
					return false;
				    } else {
					return super.doWriteLine(line);
				    }
				} else {
				    return super.doWriteLine(line);
				}
			    }

			    private String extractPackage(String line) {
				return line.replace("package", "").replace(";", "").replace(" ", "").trim();
			    }
			}.call();
			if (changed.get()) {
			    file.delete();
			    patched.renameTo(file);
			}
		    }
		} finally {
		    if (patched.exists()) {
			patched.delete();
		    }
		}
	    }
	}.call();
	if (!replacements.isEmpty()) {
	    new DirectoryVisitor(getSrcDir()) {

		@Override
		protected void onDirectory(File dir) throws Exception {
		    traverseDirectory(dir);
		}

		@Override
		protected void onFile(File file) throws Exception {
		    if (file.getName().endsWith(".java")) {
			correctImports(file);
		    }
		}

		private void correctImports(final File file) throws Exception {
		    File patched = new File(file.getPath().concat(".patched"));
		    try {
			if (patched.exists()) {
			    patched.delete();
			}
			final AtomicBoolean changed = new AtomicBoolean(false);
			new CopyReplacing(file, patched, Collections.EMPTY_MAP) {

			    private String lineReplacement;

			    @Override
			    protected synchronized String getLineReplacement() {
				return lineReplacement;
			    }

			    private synchronized void setLineReplacement(String lineReplacement) {
				this.lineReplacement = lineReplacement;
			    }

			    @Override
			    protected boolean doWriteLine(String line) {
				for (String oldPckg : replacements.keySet()) {
				    setLineReplacement(null);
				    if (line.startsWith("import " + oldPckg)) {
					String imp = "import " + replacements.get(oldPckg);
					String pckgLine = line.replace("import " + oldPckg, imp);
					System.out.println(file.getPath() + ": replacing '" + line + "' with '" + pckgLine + "'");
					changed.set(true);
					setLineReplacement(pckgLine);
					return false;
				    }
				}
				return super.doWriteLine(line);
			    }
			}.call();
			if (changed.get()) {
			    file.delete();
			    patched.renameTo(file);
			}
		    } finally {
			if (patched.exists()) {
			    patched.delete();
			}
		    }
		}
	    }.call();
	}
    }

    private final ConcurrentMap<File, MPackage> resources = new ConcurrentHashMap<File, MPackage>();
    private final ConcurrentMap<File, Boolean> toDelete = new ConcurrentHashMap<File, Boolean>();

    public final void addResource(MPackage pckg, File file, boolean deleteOnFinish) {
	if (!file.isFile()) {
	    throw new RuntimeException("only files can be used as resource");
	}
	resources.put(file, pckg);
	toDelete.put(file, deleteOnFinish);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("MBundle [name=");
	builder.append(name);
	builder.append(", srcDir=");
	builder.append(srcDir);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
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
	MBundle other = (MBundle) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }

    @Override
    public void generateCode(boolean clean) throws Exception {
	try {
	    if (showProgressbar) {
		SwingProgressBar pb = new SwingProgressBar("Generating " + getName() + " Bundle (clean = " + clean + ")");
		pb.setMax(packages.size() + bundles.size());
		pb.setDelay(10);
		pb.start();
		for (MPackage pckg : packages) {
		    pb.update("package " + pckg.getName());
		    pckg.setOverrideTemplates(overrideTemplates);
		    pckg.generateCode(clean);
		}
		for (MBundle bundle : bundles) {
		    pb.update("bundle " + bundle.getName());
		    bundle.setOverrideTemplates(overrideTemplates);
		    bundle.generateCode(false);// why not clean?!
		}
		pb.stop();
	    } else {
		for (MPackage pckg : packages) {
		    pckg.setOverrideTemplates(overrideTemplates);
		    pckg.generateCode(clean);
		}
		for (MBundle bundle : bundles) {
		    bundle.setOverrideTemplates(overrideTemplates);
		    bundle.generateCode(false);// why not clean?!
		}
	    }
	} finally {
	    for (File resource : resources.keySet()) {
		MPackage pckg = resources.get(resource);
		pckg.addResource(resource);
	    }
	    // doNotGenerateEmptyPackages
	    if (doNotGenerateEmptyPackages) {
		for (MPackage pckg : packages) {
		    File srcDir = getSrcDir();
		    File dir = pckg.getDir();
		    if (dir.listFiles().length == 0) {
			while (!dir.equals(srcDir)) {
			    dir.delete();
			    dir = dir.getParentFile();
			}
		    }
		}
	    }
	    // done
	    for (File file : toDelete.keySet()) {
		if (file.exists() && toDelete.get(file)) {
		    file.delete();
		}
	    }
	}
    }

    public static final MClass generateNewBundle(String dirPath, final String pckgName, final String className, final String srcDir) throws Exception {
	MBundle bundle = new MBundle(new File(dirPath), "generateNewBundle");
	MPackage pckg = bundle.newPackage(pckgName);
	final MClass type = pckg.newClass(className).setFinal(true);
	type.setSuperclass(MBundle.class);
	type.addConstructor().makePrivate().setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("super(\"" + srcDir + "\",\"" + className + "\");");
		return block;
	    }
	});
	MMethod init = type.addMethod("initialize", void.class).makeProtected();
	type.addExtraImport(MPackage.class);
	init.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("MPackage pckg = newPackage(\"" + pckgName + ".???\");");
		return block;
	    }
	});
	type.addMainMethod().makePublic().throwsException(Exception.class).setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("new " + className + "().generateCode(false);");
		return block;
	    }
	});
	bundle.generateCode(false);
	type.getSrcFile().setWritable(true);
	return type;
    }

    public final synchronized void setGeneratedCodeListener(MGeneratedCodeListener generatedCodeListener) {
	this.generatedCodeListener = generatedCodeListener;
    }

    public final synchronized MGeneratedCodeListener getGeneratedCodeListener() {
	return generatedCodeListener;
    }

    public List<MInterface> getAllInterfaces() {
	List<MInterface> list = new ArrayList<MInterface>();
	for (MPackage pkg : packages) {
	    list.addAll(pkg.getInterfaces());
	}
	return list;
    }

    public List<MClass> getAllClasses() {
	List<MClass> list = new ArrayList<MClass>();
	for (MPackage pkg : packages) {
	    list.addAll(pkg.getClasses());
	}
	return list;
    }

    public List<MEnum> getAllEnums() {
	List<MEnum> list = new ArrayList<MEnum>();
	for (MPackage pkg : packages) {
	    list.addAll(pkg.getEnums());
	}
	return list;
    }
}
