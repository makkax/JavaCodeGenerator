package com.cc.jcg.main;

public class Bean09
        implements NamedBean {
    
    private final String name;
    private Bean06 bean06;
    
    public Bean09(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean06 getBean06() {
        return bean06;
    }
    
    public final synchronized void setBean06(Bean06 bean06) {
        this.bean06 = bean06;
    }
}
