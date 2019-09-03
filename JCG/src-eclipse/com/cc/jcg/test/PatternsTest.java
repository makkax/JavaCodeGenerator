package com.cc.jcg.test;

import java.io.File;

import org.junit.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;

public class PatternsTest {

    @Test
    public void test() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"), "Patterns Test Bundle");
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples");
	pckg.newBean("TestBean", new MParameter(String.class, "string"), new MParameter(Integer.class, "number"));
	bundle.generateCode(false);
    }
}
