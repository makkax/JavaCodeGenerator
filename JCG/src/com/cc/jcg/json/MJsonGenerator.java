package com.cc.jcg.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.cc.jcg.MClass;
import com.cc.jcg.MFunctions;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class MJsonGenerator
	extends MFunctions {

    private final MPackage pckg;
    private final Gson gson;

    public MJsonGenerator(MPackage pckg) {
	super();
	this.pckg = pckg;
	gson = new Gson();
    }

    public MClass generateJsonBeanFor(File jsonFile, String name) throws FileNotFoundException, ClassNotFoundException {
	JsonReader reader = new JsonReader(new FileReader(jsonFile));
	Map<String, Object> json = gson.fromJson(reader, LinkedHashMap.class);
	return generateBean(name, json);
    }

    private MClass generateBean(String name, Map<String, Object> map) throws ClassNotFoundException {
	List<MParameter> fields = new ArrayList<>();
	map.forEach((key, value) -> {
	    try {
		Class<? extends Object> valueType = value.getClass();
		if (Map.class.isAssignableFrom(valueType)) {
		    MClass subtype = generateBean(camelize(key), (Map<String, Object>) value);
		    fields.add(new MParameter(subtype, key));
		} else if (Collection.class.isAssignableFrom(valueType)) {
		    Collection<?> values = (Collection<?>) value;
		    Collection<Class<?>> vTypes = new LinkedHashSet<>();
		    for (Object v : values) {
			vTypes.add(v.getClass());
		    }
		    if (vTypes.size() == 1) {
			fields.add(new MParameter(List.class, vTypes.stream().findFirst().get(), key));
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
	MClass bean = pckg.newBean(name, fields);
	return bean;
    }

    public static <T> T getBean(Class<T> beanType, File jsonFile) throws FileNotFoundException {
	Gson gson = new Gson();
	JsonReader reader = new JsonReader(new FileReader(jsonFile));
	T data = gson.fromJson(reader, beanType);
	return data;
    }
}
