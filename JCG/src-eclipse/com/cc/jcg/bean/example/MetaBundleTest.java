package com.cc.jcg.bean.example;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.cc.jcg.bean.MBeanMetaBundle;

class MetaBundleTest {

    @Test
    void test() throws Exception {
	File srcDir = new File("src-eclipse");
	Map<Class, MBeanMetaBundle> bundles = new HashMap<Class, MBeanMetaBundle>();
	MBeanMetaBundle bundle = new MBeanMetaBundle(srcDir, "com.cc.jcg.bean.example.gen", ExampleBeanA.class, bundles);
	bundle.generateCode(true);
	MBeanMetaBundle.generateMetaRegister(srcDir, "com.cc.jcg.bean.example.gen", "ExampleMetaModels", bundles);
    }
}
