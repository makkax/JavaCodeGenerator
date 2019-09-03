package com.cc.jcg.main;

public class Bean01
        implements NamedBean {
    
    private final String name;
    private Bean02 bean02;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean02 getBean02() {
        return bean02;
    }
    
    public final synchronized void setBean02(Bean02 bean02) {
        this.bean02 = bean02;
    }
}
