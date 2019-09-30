package com.cc.jcg.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cc.jcg.MClass;
import com.cc.jcg.MFunctions;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.cc.jcg.tools.Inflector;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;

public class MJsonGenerator
	extends MFunctions {

    private static final Inflector INFLECTOR = new Inflector();
    private final MPackage pckg;
    private final Gson gson;
    private final Map<String, Set<String>> maps;
    private final Map<String, MClass> classes;
    private String classesNamePrefix = "";
    private boolean addJsonBeanInterface = true;
    private boolean listOfMapsToListOfBeans = false;

    public MJsonGenerator(MPackage pckg) {
	super();
	this.pckg = pckg;
	gson = new GsonBuilder().setPrettyPrinting().create();
	maps = new HashMap<>();
	classes = new HashMap<>();
    }

    public synchronized String getClassesNamePrefix() {
	return classesNamePrefix;
    }

    public synchronized MJsonGenerator setClassesNamePrefix(String classesNamePrefix) {
	this.classesNamePrefix = classesNamePrefix;
	return this;
    }

    public synchronized boolean isAddJsonBeanInterface() {
	return addJsonBeanInterface;
    }

    public synchronized MJsonGenerator setAddJsonBeanInterface(boolean addJsonBeanInterface) {
	this.addJsonBeanInterface = addJsonBeanInterface;
	return this;
    }

    public synchronized final boolean isListOfMapsToListOfBeans() {
	return listOfMapsToListOfBeans;
    }

    public synchronized final MJsonGenerator setListOfMapsToListOfBeans(boolean listOfMapsToListOfBeans) {
	this.listOfMapsToListOfBeans = listOfMapsToListOfBeans;
	return this;
    }

    public synchronized MClass generateJsonBeanFor(File jsonFile, String name) throws FileNotFoundException, ClassNotFoundException {
	maps.clear();
	classes.clear();
	JsonReader reader = new JsonReader(new FileReader(jsonFile));
	Map<String, Object> json = gson.fromJson(reader, LinkedHashMap.class);
	return generateBean(name, json);
    }

    public synchronized MClass generateJsonBeanFor(String jsonString, String name) throws FileNotFoundException, ClassNotFoundException {
	maps.clear();
	classes.clear();
	Map<String, Object> json = gson.fromJson(jsonString, LinkedHashMap.class);
	return generateBean(name, json);
    }

    private MClass generateBean(String name, Map<String, Object> map) throws ClassNotFoundException {
	List<MParameter> fields = new ArrayList<>();
	map.forEach((key, value) -> {
	    try {
		Class<? extends Object> valueType = value.getClass();
		if (Map.class.isAssignableFrom(valueType)) {
		    Set<String> valueKeys = ((Map<String, ?>) value).keySet();
		    if (maps.containsKey(key) && maps.get(key).equals(valueKeys)) {
			MClass subtype = classes.get(key);
			fields.add(new MParameter(subtype, key));
		    } else {
			String cName = camelize(key);
			String fName = key;
			if (maps.containsKey(key) && !maps.get(key).equals(valueKeys)) {
			    int n = 2;
			    while (classes.containsKey(camelize(key) + n)) {
				n = n + 1;
			    }
			    key = key + n;
			    cName = camelize(key);
			}
			MClass subtype = generateBean(cName, (Map<String, Object>) value);
			fields.add(new MParameter(subtype, fName));
			maps.put(key, valueKeys);
			classes.put(key, subtype);
		    }
		} else if (Collection.class.isAssignableFrom(valueType)) {
		    Collection<?> values = (Collection<?>) value;
		    Collection<Class<?>> vTypes = new LinkedHashSet<>();
		    for (Object v : values) {
			vTypes.add(v.getClass());
		    }
		    if (vTypes.size() == 1) {
			Class<?> fType = vTypes.stream().findFirst().get();
			if (Map.class.isAssignableFrom(fType)) {
			    if (listOfMapsToListOfBeans) {
				String singular = INFLECTOR.camelCase(INFLECTOR.singularize(key), true);
				MClass subtype = generateBean(singular, (Map<String, Object>) values.stream().findFirst().get());
				MParameter p = new MParameter(List.class, "<" + subtype.getName() + ">", key);
				p.addImport(subtype);
				fields.add(p);
			    } else {
				String generic = "<" + fType.getSimpleName().concat("<String, Object>") + ">";
				MParameter p = new MParameter(List.class, generic, key);
				p.addImport(fType);
				fields.add(p);
			    }
			} else {
			    fields.add(new MParameter(List.class, fType, key));
			}
		    } else {
			fields.add(new MParameter(List.class, Object.class, key));
		    }
		} else {
		    fields.add(new MParameter(valueType, key));
		}
	    } catch (ClassNotFoundException e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	    }
	});
	MClass bean = createBean(classesNamePrefix + name, fields);
	if (addJsonBeanInterface) {
	    bean.addInterface(JsonBean.class);
	}
	return bean;
    }

    // can be overwritten...
    protected MClass createBean(String name, List<MParameter> fields) throws ClassNotFoundException {
	MClass cls = pckg.newBean(name, fields);
	return cls;
    }

    public static <T> T getBean(Class<T> beanType, File jsonFile) throws FileNotFoundException {
	Gson gson = new Gson();
	JsonReader reader = new JsonReader(new FileReader(jsonFile));
	T data = gson.fromJson(reader, beanType);
	return data;
    }

    public static String toJson(Object bean) throws FileNotFoundException {
	Gson gson = new Gson();
	return gson.toJson(bean);
    }

    public static void toJsonFile(Object bean, File jsonFile) throws JsonIOException, IOException {
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	FileWriter writer = new FileWriter(jsonFile);
	gson.toJson(bean, writer);
	writer.flush();
	writer.close();
    }
}
