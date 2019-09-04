package com.cc.jcg;

import java.util.function.Consumer;

public interface MCode<T extends MCode<T>>
	extends MCodeGenerator<T> {

    T setGenerator(MCodeGenerator<T> generator);

    MCodeGenerator<T> getGenerator();

    default void setCodeBlock(Consumer<MCodeBlock> consumer) {
	setGenerator(new MCodeGenerator<T>() {

	    @Override
	    public MCodeBlock getCodeBlock(T element) {
		MCodeBlock block = element.getCodeBlock(element);
		consumer.accept(block);
		return block;
	    }
	});
    }
}
