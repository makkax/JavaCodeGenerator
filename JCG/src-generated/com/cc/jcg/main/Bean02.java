package com.cc.jcg.main;

public class Bean02
        implements NamedBean {
    
    private final String name;
    private Bean07 bean;
    
    public Bean02(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean07 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean07 bean) {
        this.bean = bean;
    }
}
