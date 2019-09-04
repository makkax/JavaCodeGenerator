package com.cc.jcg.main;

public class Bean00
        implements NamedBean {
    
    private final String name;
    private Bean00 bean;
    
    public Bean00(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean00 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean00 bean) {
        this.bean = bean;
    }
}
