package com.cc.jcg.examples2;

import com.cc.jcg.MGenerated;

@MGenerated
public class MyBean {
    
    private String name;
    private MyEnum type;
    
    MyBean(String name, MyEnum type) {
    }
    
    public final synchronized String getName() {
        return name;
    }
    
    public final synchronized void setName(String name) {
        this.name = name;
    }
    
    public final synchronized MyEnum getType() {
        return type;
    }
    
    public final synchronized void setType(MyEnum type) {
        this.type = type;
    }
}
