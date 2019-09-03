package com.cc.jcg;

public class MInnerInterface
	extends MInterface
	implements MInnerType<MInterface> {

    private boolean isStatic;

    MInnerInterface(MPackage pckg, String name) {
	super(pckg, name);
    }

    public final synchronized boolean isStatic() {
	return isStatic;
    }

    public final synchronized MInnerInterface setStatic(boolean isStatic) {
	this.isStatic = isStatic;
	return this;
    }

    @Override
    protected final void afterModifier(StringBuffer before) {
	before.append(isStatic ? onEmptyNoSpace(before) + "static" : "");
    }
}
