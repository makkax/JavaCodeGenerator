package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean06 {
    
    private final String name;
    private Bean02 getBean02;
    
    public Bean06(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean02 getGetBean02() {
        return getBean02;
    }
    
    public final synchronized void setGetBean02(Bean02 getBean02) {
        this.getBean02 = getBean02;
    }
}
