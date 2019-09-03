package com.cc.jcg;

public interface MCode<T>
	extends MCodeGenerator<T> {

    T setGenerator(MCodeGenerator<T> generator);

    MCodeGenerator<T> getGenerator();
}
