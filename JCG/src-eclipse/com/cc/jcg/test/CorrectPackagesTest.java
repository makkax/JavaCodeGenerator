package com.cc.jcg.test;

import java.io.File;

import org.junit.Test;

import com.cc.jcg.MBundle;

public class CorrectPackagesTest {

    @Test
    public void test() throws Exception {
	new MBundle(new File("src"), "TestBundle").correctPackages();
    }
}
