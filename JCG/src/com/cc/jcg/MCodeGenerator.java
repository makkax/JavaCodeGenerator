package com.cc.jcg;

public interface MCodeGenerator<T> {

    MCodeBlock getCodeBlock(T element);
}
