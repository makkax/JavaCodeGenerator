package com.cc.jcg.main;

public class Bean09
        implements NamedBean {
    
    private final String name;
    private Bean00 bean;
    
    public Bean09(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean00 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean00 bean) {
        this.bean = bean;
    }
}
