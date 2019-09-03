package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
public class Bean01 {
    
    private final String name;
    private Bean04 getBean04;
    
    public Bean01(String name) {
        super();
        this.name = name;
    }
    
    public final synchronized Bean04 getGetBean04() {
        return getBean04;
    }
    
    public final synchronized void setGetBean04(Bean04 getBean04) {
        this.getBean04 = getBean04;
    }
}
