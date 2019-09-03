package com.cc.jcg.test;

import java.io.File;

import org.junit.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MClass;
import com.cc.jcg.MCodeBlock;
import com.cc.jcg.MCodeGenerator;
import com.cc.jcg.MConstructor;
import com.cc.jcg.MEnum;
import com.cc.jcg.MInterface;
import com.cc.jcg.MPackage;

public class BundleTest {

    @Test
    public void test() throws Exception {
	MBundle bundle = new MBundle(new File("src-generated"), "Example Bundle");
	MPackage pckg = bundle.newPackage("com.cc.jcg.examples");
	MClass cls = pckg.newClass("SomeClass");
	cls.makePublic().setFinal(true);
	cls.addConstructor().setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("super();");
		block.addLine("// do other stuffs");
		return block;
	    }
	});
	MInterface intf = pckg.newInterface("SomeInterface");
	intf.makeDefault().setGeneric("<T>");
	MEnum enm = pckg.newEnum("SomeEnum");
	enm.addValues("V01", "V02");
	enm.addInterface(intf);
	bundle.generateCode(false);
    }
}
