package com.cc.jcg.examples3;

import com.cc.jcg.proxy.Example;

public class TestExampleProxy
        implements Example {
    
    private final Example object;
    
    public TestExampleProxy(Example object) {
        super();
        this.object = object;
    }
    
    @Override
    public double getSomeValue() {
        return object.getSomeValue();
    }
    
    @Override
    public void doSomething(String arg0) {
        object.doSomething(arg0);
    }
}
