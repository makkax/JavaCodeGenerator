package com.cc.jcg.examples2;

import com.cc.jcg.MGenerated;
import com.cc.jcg.proxy.Example;

@MGenerated
public class TestExampleProxy
        implements Example {
    
    private final Example object;
    
    public TestExampleProxy(Example object) {
        super();
        this.object = object;
    }
    
    @Override
    public void doSomething(String arg0) {
        object.doSomething(arg0);
    }
    
    @Override
    public double getSomeValue() {
        return object.getSomeValue();
    }
}
