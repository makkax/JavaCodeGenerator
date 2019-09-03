package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean09 {
    
    private final String name;
    private Bean08 getBean08;
    
    public Bean09(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean08 getGetBean08() {
        return getBean08;
    }
    
    public final synchronized void setGetBean08(Bean08 getBean08) {
        this.getBean08 = getBean08;
    }
}
