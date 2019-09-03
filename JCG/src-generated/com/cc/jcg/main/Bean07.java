package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean07 {
    
    private final String name;
    private Bean06 getBean06;
    
    public Bean07(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean06 getGetBean06() {
        return getBean06;
    }
    
    public final synchronized void setGetBean06(Bean06 getBean06) {
        this.getBean06 = getBean06;
    }
}
