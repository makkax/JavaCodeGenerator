package com.cc.jcg.test.types;

public interface TypeWithGeneric<GENERIC> {

    Integer get();

    void set(GENERIC some);
}
