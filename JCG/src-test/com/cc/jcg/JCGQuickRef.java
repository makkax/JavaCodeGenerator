package com.cc.jcg;

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
	// -------------------------------------------------------------------------------------------------
	// General configurations
	// -------------------------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);// default true
	MBundle.GENERATE_READONLY.set(false);// default true
	MBundle.DO_NOT_GENERATE_EMPTY_PACKAGES.set(true);// default false
	// -------------------------------------------------------------------------------------------------
	// MBundle
	// -------------------------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	bundle.setShowProgressbar(true);// default false
	bundle.setOverrideTemplates(false);// default false
	bundle.setDoNotGenerateEmptyPackages(true);// default false
	// -------------------------------------------------------------------------------------------------
	bundle.getPackages();
	bundle.getPackageByName("com.jcg.gen");
	bundle.getAllInterfaces();
	bundle.getInterfaceByName("SomeMInterface");// the first one that finds...
	bundle.getAllClasses();
	bundle.getClassByName("SomeMClass");// the first one that finds...
	bundle.getAllEnums();
	bundle.getEnumByName("SomeMEnum");// the first one that finds...
	// -------------------------------------------------------------------------------------------------
	MGeneratedCodeListener listener = new MGeneratedCodeListener() {

	    @Override
	    public void onNew(MAnnotation type) {
		System.out.println(type);
	    }

	    @Override
	    public void on(MAnnotation type, String line) {
	    }

	    @Override
	    public void onNew(MInterface type) {
		System.out.println(type);
	    }

	    @Override
	    public void on(MInterface type, String line) {
	    }

	    @Override
	    public void onNew(MClass type) {
		System.out.println(type);
	    }

	    @Override
	    public void on(MClass type, String line) {
	    }

	    @Override
	    public void onNew(MEnum type) {
		System.out.println(type);
	    }

	    @Override
	    public void on(MEnum type, String line) {
	    }

	    @Override
	    public void onNew(MJavaFile type) {
		System.out.println(type);
	    }

	    @Override
	    public void on(MJavaFile type, String line) {
	    }
	};
	bundle.setGeneratedCodeListener(listener);
	// -------------------------------------------------------------------------------------------------
	bundle.generateCode(false);// clean = false, when true deletes everything else in each package
	// -------------------------------------------------------------------------------------------------
	// MPackage
	// -------------------------------------------------------------------------------------------------
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	MPackage spckg = pckg.newSubPackage("test");// com.cc.jcg.main.test
	// -------------------------------------------------------------------------------------------------
	pckg.setFailOnRepeatedDefinitions(true);// default true
	// -------------------------------------------------------------------------------------------------
	// pckg.newAnnotation(name, annType, annRetention);
	// pckg.newAnnotationRuntime(name, annType);
	// pckg.newAnnotationSource(name, annType);
	// -------------------------------------------------------------------------------------------------
	// pckg.newBean(String name, MParameter... fields);
	// pckg.newBean(String name, Collection<MParameter> fields);
	// -------------------------------------------------------------------------------------------------
	// pckg.newClass(name);
	// pckg.newClass(intf, name);
	// pckg.newSubclass(supertype, name);
	// -------------------------------------------------------------------------------------------------
	// pckg.newDelegatorClass(MType wrapped);
	// pckg.newWrapperClass(wrapped, delegatePublicMethods);
	// pckg.newWrapperClass(name, wrapped, delegatePublicMethods);
	// -------------------------------------------------------------------------------------------------
	// pckg.newEnum(name);
	// pckg.newEnumDispatcher(eType, name);
	// -------------------------------------------------------------------------------------------------
	// pckg.newTypeSafeEnum(name, values);
	// pckg.newTypeSafeEnum(name, mEum);
	// -------------------------------------------------------------------------------------------------
	// pckg.newInterface(name);
	// pckg.newInterface(supertype, name);
	// -------------------------------------------------------------------------------------------------
    }
}
