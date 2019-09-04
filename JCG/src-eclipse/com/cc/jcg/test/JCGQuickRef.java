package com.cc.jcg.test;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.cc.jcg.MAnnotation;
import com.cc.jcg.MBundle;
import com.cc.jcg.MClass;
import com.cc.jcg.MEnum;
import com.cc.jcg.MGeneratedCodeListener;
import com.cc.jcg.MInterface;
import com.cc.jcg.MJavaFile;
import com.cc.jcg.MPackage;

public class JCGQuickRef {

    @Test
    void test() throws Exception {
	// ----------------------------------------------------------------------------------
	// General configurations
	// ----------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);// default true
	MBundle.GENERATE_READONLY.set(false);// default true
	MBundle.DO_NOT_GENERATE_EMPTY_PACKAGES.set(true);// default false
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	bundle.setShowProgressbar(true);// default false
	bundle.setOverrideTemplates(false);// default false
	bundle.setDoNotGenerateEmptyPackages(true);// default false
	// ----------------------------------------------------------------------------------
	bundle.getPackages();
	bundle.getAllInterfaces();
	bundle.getAllClasses();
	bundle.getAllEnums();
	// ----------------------------------------------------------------------------------
	MGeneratedCodeListener listener = new MGeneratedCodeListener() {

	    @Override
	    public void onNew(MAnnotation type) {
	    }

	    @Override
	    public void onNew(MInterface type) {
	    }

	    @Override
	    public void on(MInterface type, String line) {
	    }

	    @Override
	    public void on(MAnnotation type, String line) {
	    }

	    @Override
	    public void onNew(MClass type) {
	    }

	    @Override
	    public void on(MClass type, String line) {
	    }

	    @Override
	    public void onNew(MEnum type) {
	    }

	    @Override
	    public void on(MEnum type, String line) {
	    }

	    @Override
	    public void onNew(MJavaFile type) {
	    }

	    @Override
	    public void on(MJavaFile type, String line) {
	    }
	};
	bundle.setGeneratedCodeListener(listener);
	// ----------------------------------------------------------------------------------
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
    }
}
