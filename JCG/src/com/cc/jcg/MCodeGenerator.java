package com.cc.jcg;

@FunctionalInterface
public interface MCodeGenerator<T> {

    MCodeBlock getCodeBlock(T element);
}
