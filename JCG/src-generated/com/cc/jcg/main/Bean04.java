package com.cc.jcg.main;

public class Bean04
        implements NamedBean {
    
    private final String name;
    private Bean05 bean;
    
    public Bean04(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean05 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean05 bean) {
        this.bean = bean;
    }
}
