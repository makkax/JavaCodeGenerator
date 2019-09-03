package com.cc.jcg.test;

import java.io.File;

import org.junit.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MPackage;
import com.cc.jcg.proxy.Example;
import com.cc.jcg.proxy.MGenerateDelegator;
import com.cc.jcg.proxy.MGenerateMethodsProxy;

public class MGenerateProxyTest {

    @Test
    public void test1() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples3");
	MGenerateDelegator generator = new MGenerateDelegator(pckg, Example.class);
	generator.generate("TestExampleProxy");
	bundle.generateCode(false);
    }

    @Test
    public void test2() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples3");
	MGenerateMethodsProxy generator = new MGenerateMethodsProxy(pckg, Example.class);
	generator.generate("TestExampleMethodsProxy");
	bundle.generateCode(false);
    }
}