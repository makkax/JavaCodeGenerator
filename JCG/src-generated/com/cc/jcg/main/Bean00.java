package com.cc.jcg.main;

public class Bean00
        implements NamedBean {
    
    private final String name;
    private Bean09 bean;
    
    public Bean00(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean09 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean09 bean) {
        this.bean = bean;
    }
}
