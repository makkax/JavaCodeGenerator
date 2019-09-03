package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean03 {
    
    private final String name;
    private Bean03 getBean03;
    
    public Bean03(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean03 getGetBean03() {
        return getBean03;
    }
    
    public final synchronized void setGetBean03(Bean03 getBean03) {
        this.getBean03 = getBean03;
    }
}
