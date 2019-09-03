package com.cc.jcg;

public class MInnerEnum
	extends MEnum
	implements MInnerType<MEnum> {

    MInnerEnum(MPackage pckg, String name) {
	super(pckg, name);
    }
}
