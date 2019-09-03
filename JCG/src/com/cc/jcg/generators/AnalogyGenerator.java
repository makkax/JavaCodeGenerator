package com.cc.jcg.generators;

import java.io.File;
import java.io.FileFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.cc.jcg.tools.CopyReplacing;

public class AnalogyGenerator {

    private final File templatesDir;
    private final FileFilter templatesFilter;
    private final Map<String, String> substitutions;
    private final Map<String, String> packages;
    private boolean overwrite = true;

    public AnalogyGenerator(File templatesDir) {
	this(templatesDir, new FileFilter() {

	    @Override
	    public boolean accept(File file) {
		return file.isFile() && file.getName().endsWith(".java");
	    }
	});
    }

    public AnalogyGenerator(File templatesDir, FileFilter templatesFilter) {
	super();
	this.templatesDir = templatesDir;
	this.templatesFilter = templatesFilter;
	substitutions = new LinkedHashMap<String, String>();
	packages = new LinkedHashMap<String, String>();
    }

    public final synchronized boolean isOverwrite() {
	return overwrite;
    }

    public final synchronized void setOverwrite(boolean overwrite) {
	this.overwrite = overwrite;
    }

    public final Map<String, String> getSubstitutions() {
	return Collections.synchronizedMap(substitutions);
    }

    public final Map<String, String> getPackages() {
	return Collections.synchronizedMap(packages);
    }

    public final void addSubstitution(String source, String target) {
	substitutions.put(source, target);
    }

    public final void mapPackage(String source, String target) {
	packages.put(source, target);
    }

    private TreeMap<Integer, Map<String, String>> sortedSubstitutions() {
	return getSortedMap(substitutions);
    }

    private TreeMap<Integer, Map<String, String>> sortedPackages() {
	return getSortedMap(packages);
    }

    private TreeMap<Integer, Map<String, String>> getSortedMap(Map<String, String> input) {
	TreeMap<Integer, Map<String, String>> map = new TreeMap<Integer, Map<String, String>>();
	for (String key : input.keySet()) {
	    int length = -key.length();
	    if (!map.containsKey(length)) {
		map.put(length, new LinkedHashMap<String, String>());
	    }
	    map.get(length).put(key, input.get(key));
	}
	return map;
    }

    public void generate(File targetDir) throws Exception {
	targetDir.mkdirs();
	TreeMap<Integer, Map<String, String>> sortedSubstitutions = sortedSubstitutions();
	TreeMap<Integer, Map<String, String>> sortedPackages = sortedPackages();
	Map<String, String> fileNames = new HashMap<String, String>();
	for (File source : templatesDir.listFiles(templatesFilter)) {
	    File target = new File(targetDir, replace(source.getName(), sortedSubstitutions));
	    fileNames.put(source.getName(), target.getName());
	    fileNames.put(source.getPath(), target.getPath());
	    fileNames.put(source.getAbsolutePath(), target.getAbsolutePath());
	}
	TreeMap<Integer, Map<String, String>> sortedFileNames = getSortedMap(fileNames);
	for (File source : templatesDir.listFiles(templatesFilter)) {
	    File target = new File(targetDir, replace(source.getName(), sortedSubstitutions));
	    if (overwrite && target.exists()) {
		target.delete();
	    } else if (!overwrite && target.exists()) {
		continue;
	    }
	    LinkedHashMap<String, String> replacements = new LinkedHashMap<String, String>();
	    for (Integer lenght : sortedPackages.keySet()) {
		Map<String, String> map = sortedPackages.get(lenght);
		for (String key : map.keySet()) {
		    replacements.put("package ${1};".replace("${1}", key), "package ${1};".replace("${1}", map.get(key)));
		    replacements.put("import ${1};".replace("${1}", key), "import ${1};".replace("${1}", map.get(key)));
		}
	    }
	    for (Integer lenght : sortedFileNames.keySet()) {
		Map<String, String> map = sortedFileNames.get(lenght);
		for (String key : map.keySet()) {
		    replacements.put("package ${1};".replace("${1}", key), "package ${1};".replace("${1}", map.get(key)));
		    replacements.put("import ${1};".replace("${1}", key), "import ${1};".replace("${1}", map.get(key)));
		    replacements.put(key, map.get(key));
		}
	    }
	    for (Integer lenght : sortedSubstitutions.keySet()) {
		Map<String, String> map = sortedSubstitutions.get(lenght);
		for (String key : sortedSubstitutions.get(lenght).keySet()) {
		    replacements.put(key, map.get(key));
		}
	    }
	    new CopyReplacing(source, target, replacements).call();
	}
    }

    private String replace(String s, TreeMap<Integer, Map<String, String>> sorteds) {
	String t = s;
	for (Integer lenght : sorteds.keySet()) {
	    Map<String, String> map = sorteds.get(lenght);
	    for (String key : map.keySet()) {
		t = t.replace(key, map.get(key));
	    }
	}
	return t;
    }
}
