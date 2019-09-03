package com.cc.jcg.main;

public class Bean03
        implements NamedBean {
    
    private final String name;
    private Bean09 bean09;
    
    public Bean03(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean09 getBean09() {
        return bean09;
    }
    
    public final synchronized void setBean09(Bean09 bean09) {
        this.bean09 = bean09;
    }
}
