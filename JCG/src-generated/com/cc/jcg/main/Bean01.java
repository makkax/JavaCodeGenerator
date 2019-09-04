package com.cc.jcg.main;

public class Bean01
        implements NamedBean {
    
    private final String name;
    private Bean08 bean;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean08 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean08 bean) {
        this.bean = bean;
    }
}
