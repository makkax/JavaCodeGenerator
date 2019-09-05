package com.cc.jcg.tools.reflection;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;

public class JavaReflectionToolsFastClasspathScanner
	implements JavaReflectionTools {

    JavaReflectionToolsFastClasspathScanner() {
	super();
    }

    @Override
    public <T> List<Class<? extends T>> getSubTypesOf(Class<? extends T> type, String pckg, Boolean isAbstract) {
	boolean log = JavaReflectionTools.LOG.get();
	if (log) {
	    System.out.println("getSubTypesOf " + type + " in " + pckg + ", isAbstract " + isAbstract);
	}
	List<Class<? extends T>> types = new ArrayList<>();
	if (type.isInterface()) {
	    new FastClasspathScanner(pckg)
		    .matchClassesImplementing(type, c -> {
			if (log) {
			    System.out.println("? " + c);
			}
			if (isAbstract == null || isAbstract.equals(Modifier.isAbstract(c.getModifiers()))) {
			    types.add(c);
			}
		    })
		    .scan();
	} else {
	    new FastClasspathScanner(pckg)
		    .matchSubclassesOf(type, c -> {
			if (log) {
			    System.out.println("? " + c);
			}
			if (isAbstract == null || isAbstract.equals(Modifier.isAbstract(c.getModifiers()))) {
			    types.add(c);
			}
		    })
		    .scan();
	}
	return types;
    }
}
