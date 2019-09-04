package com.cc.jcg.examples2;

import com.cc.jcg.MTemplate;

@MTemplate
public class MyBeanDelegate {
    
    private final MyBean wrapped;
    
    public MyBeanDelegate(MyBean wrapped) {
        super();
        this.wrapped = wrapped;
    }
    
    public final MyBean getWrapped() {
        return wrapped;
    }
    
    public String getName() {
        return wrapped.getName();
    }
    
    public void setName(String name) {
        wrapped.setName(name);
    }
    
    public MyEnum getType() {
        return wrapped.getType();
    }
    
    public void setType(MyEnum type) {
        wrapped.setType(type);
    }
}
