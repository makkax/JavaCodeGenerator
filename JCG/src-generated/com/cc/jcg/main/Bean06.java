package com.cc.jcg.main;

public class Bean06
        implements NamedBean {
    
    private final String name;
    private Bean03 bean03;
    
    public Bean06(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean03 getBean03() {
        return bean03;
    }
    
    public final synchronized void setBean03(Bean03 bean03) {
        this.bean03 = bean03;
    }
}
