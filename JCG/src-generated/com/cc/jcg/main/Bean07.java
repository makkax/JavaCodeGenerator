package com.cc.jcg.main;

public class Bean07
        implements NamedBean {
    
    private final String name;
    private Bean05 bean05;
    
    public Bean07(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean05 getBean05() {
        return bean05;
    }
    
    public final synchronized void setBean05(Bean05 bean05) {
        this.bean05 = bean05;
    }
}
