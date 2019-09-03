package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean00 {
    
    private final String name;
    private Bean07 getBean07;
    
    public Bean00(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean07 getGetBean07() {
        return getBean07;
    }
    
    public final synchronized void setGetBean07(Bean07 getBean07) {
        this.getBean07 = getBean07;
    }
}
