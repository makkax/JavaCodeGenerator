package com.cc.jcg;

public class MInnerClass
	extends MClass
	implements MInnerType<MClass> {

    private boolean isStatic;

    MInnerClass(MPackage pckg, String name) {
	super(pckg, name);
    }

    public final synchronized boolean isStatic() {
	return isStatic;
    }

    public final synchronized MInnerClass setStatic(boolean isStatic) {
	this.isStatic = isStatic;
	return this;
    }

    @Override
    protected final void afterModifier(StringBuffer before) {
	before.append(isStatic ? onEmptyNoSpace(before) + "static" : "");
    }
}
