package com.cc.jcg.main;

public class Bean05
        implements NamedBean {
    
    private final String name;
    private Bean00 bean00;
    
    public Bean05(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean00 getBean00() {
        return bean00;
    }
    
    public final synchronized void setBean00(Bean00 bean00) {
        this.bean00 = bean00;
    }
}
