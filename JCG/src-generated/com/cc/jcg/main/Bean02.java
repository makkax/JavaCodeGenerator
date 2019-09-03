package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean02 {
    
    private final String name;
    private Bean01 getBean01;
    
    public Bean02(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean01 getGetBean01() {
        return getBean01;
    }
    
    public final synchronized void setGetBean01(Bean01 getBean01) {
        this.getBean01 = getBean01;
    }
}
