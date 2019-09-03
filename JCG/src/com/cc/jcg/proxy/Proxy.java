package com.cc.jcg.proxy;

public interface Proxy<O> {

    void onMethod(O object, ProxyMethod method);
}
