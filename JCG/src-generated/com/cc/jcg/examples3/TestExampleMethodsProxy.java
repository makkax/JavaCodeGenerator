package com.cc.jcg.examples3;

import com.cc.jcg.proxy.Example;
import com.cc.jcg.proxy.PM;
import com.cc.jcg.proxy.Proxy;
import com.cc.jcg.proxy.ProxyMethod;

public class TestExampleMethodsProxy
        implements Example {
    
    private final Example object;
    private final Proxy<Example> proxy;
    
    public TestExampleMethodsProxy(Example object, Proxy<Example> proxy) {
        super();
        this.object = object;
        this.proxy = proxy;
    }
    
    @Override
    @PM(value = 1)
    public void doSomething(String arg0) {
        ProxyMethod method = new ProxyMethod(TestExampleMethodsProxy.class, 1);
        // parameters
        method.getParameter("arg0").setValue(arg0);
        // no return value
        object.doSomething(arg0);
        proxy.onMethod(object, method);
    }
    
    @Override
    @PM(value = 2)
    public double getSomeValue() {
        ProxyMethod method = new ProxyMethod(TestExampleMethodsProxy.class, 2);
        // no parameters
        // return value
        double value = object.getSomeValue();
        method.setValue(value);
        proxy.onMethod(object, method);
        return value;
    }
}
