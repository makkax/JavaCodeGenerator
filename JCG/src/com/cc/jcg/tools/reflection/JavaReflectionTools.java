package com.cc.jcg.tools.reflection;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public interface JavaReflectionTools {

    public static final AtomicBoolean LOG = new AtomicBoolean(false);
    public static final JavaReflectionTools INSTANCE = new JavaReflectionToolsFastClasspathScanner();

    <T> List<Class<? extends T>> getSubTypesOf(Class<? extends T> type, String pckg, Boolean isAbstract);

    default <T> List<Class<? extends T>> getFilteredSubTypesOf(Class<? extends T> type, String pckg, Predicate<Class> predicate) {
	return getSubTypesOf(type, pckg).stream().filter(predicate).collect(Collectors.toList());
    }

    default <T> List<Class<? extends T>> getSubTypesOf(Class<? extends T> type, String pckg) {
	return getSubTypesOf(type, pckg, null);
    }

    default <T> List<Class<? extends T>> getInterfacesImplementing(Class<? extends T> type, String pckg) {
	Predicate<Class> predicate = Class::isInterface;
	return getSubTypesOf(type, pckg).stream().filter(predicate).collect(Collectors.toList());
    }

    default <T> List<Class<? extends T>> getClassesImplementing(Class<? extends T> type, String pckg, Boolean isAbstract) {
	Predicate<Class> predicate = Class::isInterface;
	return getSubTypesOf(type, pckg, isAbstract).stream().filter(predicate.negate()).collect(Collectors.toList());
    }

    default <T> List<Class<? extends T>> getClassesExtending(Class<? extends T> type, String pckg, Boolean isAbstract) {
	return getClassesImplementing(type, pckg, isAbstract);
    }

    default <T> List<Class<? extends T>> getAbstractSubTypesOf(Class<? extends T> type, String pckg) {
	return getSubTypesOf(type, pckg, true);
    }

    default <T> List<Class<? extends T>> getNotAbstractSubTypesOf(Class<? extends T> type, String pckg) {
	return getSubTypesOf(type, pckg, false);
    }
}
