package com.cc.jcg.examples2;

import com.cc.jcg.MTemplate;

@MTemplate
public final class SpecialEnum {
    
    public static final SpecialEnum READY = new SpecialEnum("READY");
    public static final SpecialEnum DRAFT = new SpecialEnum("DRAFT");
    public static final SpecialEnum CANCELLED = new SpecialEnum("CANCELLED");
    public static final SpecialEnum CLOSED = new SpecialEnum("CLOSED");
    
    private final String name;
    
    private SpecialEnum(String name) {
        super();
        this.name = name;
    }
    
    public String name() {
        return name;
    }
}
