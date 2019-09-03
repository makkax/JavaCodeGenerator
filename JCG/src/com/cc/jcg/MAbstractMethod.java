package com.cc.jcg;

public class MAbstractMethod
	extends MMethod {

    MAbstractMethod(MType container, String name, Class<?> returnType, MParameter... parameters) {
	super(container, name, returnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, Class<?> returnType, String genericReturnType, MParameter... parameters) {
	super(container, name, returnType, genericReturnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, MType returnType, MParameter... parameters) {
	super(container, name, returnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, MType returnType, String genericReturnType, MParameter... parameters) {
	super(container, name, returnType, genericReturnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, MTypeRef returnType, MParameter... parameters) {
	super(container, name, returnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, MTypeRef returnType, String genericReturnType, MParameter... parameters) {
	super(container, name, returnType, genericReturnType, parameters);
	setAbstract(true);
    }

    MAbstractMethod(MType container, String name, String genericReturnType, MParameter... parameters) {
	super(container, name, genericReturnType, parameters);
	setAbstract(true);
    }

    @Override
    public final synchronized MMethod setGenerator(MCodeGenerator<MMethod> generator) {
	throw new RuntimeException("invalid usage: an abstract method can not have a body");
    }
}
