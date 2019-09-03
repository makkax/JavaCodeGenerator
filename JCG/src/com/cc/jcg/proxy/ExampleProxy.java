package com.cc.jcg.proxy;

import com.cc.jcg.MGenerated;

@MGenerated
public class ExampleProxy
	implements Example {

    private final Example object;

    public ExampleProxy(Example object) {
	super();
	this.object = object;
    }

    @Override
    public double getSomeValue() {
	return object.getSomeValue();
    }

    @Override
    public void doSomething(String arg) {
	object.doSomething(arg);
    }
}
