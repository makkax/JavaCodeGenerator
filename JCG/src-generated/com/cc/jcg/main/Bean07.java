package com.cc.jcg.main;

public class Bean07
        implements NamedBean {
    
    private final String name;
    private Bean01 bean;
    
    public Bean07(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean01 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean01 bean) {
        this.bean = bean;
    }
}
