package com.cc.jcg.main;

public class Bean04
        implements NamedBean {
    
    private final String name;
    private Bean07 bean07;
    
    public Bean04(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean07 getBean07() {
        return bean07;
    }
    
    public final synchronized void setBean07(Bean07 bean07) {
        this.bean07 = bean07;
    }
}
