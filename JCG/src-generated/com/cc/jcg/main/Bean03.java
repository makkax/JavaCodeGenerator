package com.cc.jcg.main;

public class Bean03
        implements NamedBean {
    
    private final String name;
    private Bean06 bean;
    
    public Bean03(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean06 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean06 bean) {
        this.bean = bean;
    }
}
