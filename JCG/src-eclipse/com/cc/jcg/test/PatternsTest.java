package com.cc.jcg.test;

import java.io.File;

import org.junit.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.cc.jcg.proxy.Example;
import com.cc.jcg.proxy.MGenerateDelegator;
import com.cc.jcg.proxy.MGenerateMethodsProxy;

public class PatternsTest {

    @Test
    public void test() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"), "Patterns Test Bundle");
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples");
	pckg.newBean("TestBean", new MParameter(String.class, "string"), new MParameter(Integer.class, "number"));
	bundle.generateCode(false);
    }

    @Test
    public void test2() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples3");
	MGenerateDelegator generator = new MGenerateDelegator(pckg, Example.class);
	generator.generate("TestExampleProxy");
	bundle.generateCode(false);
    }

    @Test
    public void test3() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples3");
	MGenerateMethodsProxy generator = new MGenerateMethodsProxy(pckg, Example.class);
	generator.generate("TestExampleMethodsProxy");
	bundle.generateCode(false);
    }
}
