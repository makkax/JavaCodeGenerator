package com.cc.jcg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class MainExample {

    @Test
    void test() throws Exception {
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"), "Main Example Bundle");
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
	List<MClass> classes = new ArrayList<>();
	for (int i = 0; i < 10; i++) {
	    MClass cls = pckg.newClass("Bean0" + i);
	    classes.add(cls);
	}
	for (MClass cls : classes) {
	    MField name = cls.addField(String.class, "name").setFinal(true);
	    cls.addFinalFieldsConstructor();
	    int rand = Double.valueOf(10 * Math.random()).intValue();
	    MField field = cls.addField(classes.get(rand), "getBean0" + rand);
	    field.addAccessorMethods();
	}
	// ----------------------------------------------------------------------------------
	bundle.generateCode(true);
	// ----------------------------------------------------------------------------------
    }
}
