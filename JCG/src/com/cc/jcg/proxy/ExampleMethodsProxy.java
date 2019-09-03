package com.cc.jcg.proxy;

import com.cc.jcg.MGenerated;

@MGenerated
public class ExampleMethodsProxy
	implements Example {

    private final Example object;
    private final Proxy<Example> proxy;

    public ExampleMethodsProxy(Example object, Proxy<Example> proxy) {
	super();
	this.object = object;
	this.proxy = proxy;
    }

    @Override
    @PM(1)
    public double getSomeValue() {
	ProxyMethod method = new ProxyMethod(ExampleMethodsProxy.class, 1);
	// no parameters
	// return value
	double value = object.getSomeValue();
	method.setValue(value);
	proxy.onMethod(object, method);
	return value;
    }

    @Override
    @PM(2)
    public void doSomething(String arg) {
	ProxyMethod method = new ProxyMethod(ExampleMethodsProxy.class, 2);
	// parameters
	method.getParameter("arg").setValue(arg);
	// no return value
	object.doSomething(arg);
	proxy.onMethod(object, method);
    }
}
