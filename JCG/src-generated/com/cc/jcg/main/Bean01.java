package com.cc.jcg.main;

public class Bean01
        implements NamedBean {
    
    private final String name;
    private Bean09 bean;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean09 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean09 bean) {
        this.bean = bean;
    }
}
