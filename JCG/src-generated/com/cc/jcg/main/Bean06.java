package com.cc.jcg.main;

public class Bean06
        implements NamedBean {
    
    private final String name;
    private Bean03 bean;
    
    public Bean06(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean03 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean03 bean) {
        this.bean = bean;
    }
}
