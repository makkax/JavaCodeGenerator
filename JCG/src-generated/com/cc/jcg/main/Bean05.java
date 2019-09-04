package com.cc.jcg.main;

public class Bean05
        implements NamedBean {
    
    private final String name;
    private Bean04 bean;
    
    public Bean05(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean04 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean04 bean) {
        this.bean = bean;
    }
}
