package com.cc.jcg.main;

public class Bean08
        implements NamedBean {
    
    private final String name;
    private Bean04 bean04;
    
    public Bean08(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean04 getBean04() {
        return bean04;
    }
    
    public final synchronized void setBean04(Bean04 bean04) {
        this.bean04 = bean04;
    }
}
