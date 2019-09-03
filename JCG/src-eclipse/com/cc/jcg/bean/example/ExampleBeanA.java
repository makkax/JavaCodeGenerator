package com.cc.jcg.bean.example;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.cc.jcg.bean.MBBean;

@MBBean
public final class ExampleBeanA
	implements Serializable {

    private static final long serialVersionUID = 1700124504394968533L;
    private String name;
    private Date created;
    private int age;
    private boolean manager;
    private Set<String> values;
    private ExampleBeanB ref;
    private LinkedList<ExampleBeanB> refs;

    public synchronized String getName() {
	return name;
    }

    public synchronized void setName(String name) {
	this.name = name;
    }

    public synchronized Date getCreated() {
	return created;
    }

    public synchronized void setCreated(Date created) {
	this.created = created;
    }

    public synchronized int getAge() {
	return age;
    }

    public synchronized void setAge(int age) {
	this.age = age;
    }

    public synchronized boolean isManager() {
	return manager;
    }

    public synchronized void setManager(boolean manager) {
	this.manager = manager;
    }

    public synchronized Set<String> getValues() {
	return values;
    }

    public synchronized void setValues(Set<String> values) {
	this.values = values;
    }

    public synchronized ExampleBeanB getRef() {
	return ref;
    }

    public synchronized void setRef(ExampleBeanB ref) {
	this.ref = ref;
    }

    public synchronized List<ExampleBeanB> getRefs() {
	return refs;
    }

    public synchronized void setRefs(LinkedList<ExampleBeanB> refs) {
	this.refs = refs;
    }
}
