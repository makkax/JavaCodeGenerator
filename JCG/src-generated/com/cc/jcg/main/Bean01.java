package com.cc.jcg.main;

public class Bean01
        implements NamedBean {
    
    private final String name;
    private Bean06 bean;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean06 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean06 bean) {
        this.bean = bean;
    }
}
