package com.cc.jcg.main;

public class Bean01
        implements NamedBean {
    
    private final String name;
    private Bean01 bean01;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean01 getBean01() {
        return bean01;
    }
    
    public final synchronized void setBean01(Bean01 bean01) {
        this.bean01 = bean01;
    }
}
