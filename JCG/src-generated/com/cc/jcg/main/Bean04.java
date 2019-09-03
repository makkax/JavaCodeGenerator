package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean04 {
    
    private final String name;
    private Bean05 getBean05;
    
    public Bean04(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean05 getGetBean05() {
        return getBean05;
    }
    
    public final synchronized void setGetBean05(Bean05 getBean05) {
        this.getBean05 = getBean05;
    }
}
