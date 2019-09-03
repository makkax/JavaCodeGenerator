package com.cc.jcg.bean;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class MBRegistry {

    private static final ConcurrentMap<String, MBBeanMeta> REGISTRY = new ConcurrentHashMap<String, MBBeanMeta>();
    private static final ConcurrentMap<Class<?>, MBBeanMeta> BEANS = new ConcurrentHashMap<Class<?>, MBBeanMeta>();

    static <BM extends MBBeanMeta> BM getBeanMeta(String key) {// key = meta.getClass().getName()
	return (BM) REGISTRY.get(key);
    }

    static MBBeanMeta registerBeanMeta(MBBeanMeta meta) {
	String key = meta.getClass().getName();
	if (REGISTRY.putIfAbsent(key, meta) == null) {
	    System.out.println("REGISTRY[" + key + "]<< " + meta);
	    BEANS.putIfAbsent(meta.getBeanType(), meta);
	}
	return REGISTRY.get(meta.getBeanType());
    }

    public static TreeMap<Class<?>, MBBeanMeta> get() {
	return new TreeMap<Class<?>, MBBeanMeta>(BEANS);
    }

    public static MBBeanMeta getBeanMetaByName(Class<? extends MBBeanMeta> type) {
	return REGISTRY.get(type.getName());
    }

    public static MBBeanMeta getBeanMetaByBeanType(Class<?> type) {
	return BEANS.get(type);
    }

    private MBRegistry() {
	super();
    }
}
