package com.cc.jcg;

public interface MEnumDispatcher<E extends Enum<E>, R> {

    R dispatch(E value);
}
