package com.cc.jcg;

import java.io.File;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.cc.jcg.MClass.MClassModifier;

class MainExamples {

    @Test
    void test01() throws Exception {
	// ----------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
	Collection<MParameter> fields = new ArrayList<>();
	fields.add(new MParameter(String.class, "name"));
	fields.add(new MParameter(String.class, "email"));
	fields.add(new MParameter(LocalDate.class, "birthday"));
	fields.add(new MParameter(boolean.class, "active"));
	MClass bean = pckg.newBean("User", fields);
	// ----------------------------------------------------------------------------------
	MMethod activate = bean.addMethod("activate", void.class);
	activate.setFinal(true);
	activate.setSynchronized(true);
	StringBuffer code = new StringBuffer();
	code.append("active = true;\n");
	code.append("// TODO: sendActivationEmail(email);");
	activate.setBlockContent(code);
	// ----------------------------------------------------------------------------------
	MMethod deactivate = bean.addMethod("deactivate", void.class);
	deactivate.setFinal(true);
	deactivate.setSynchronized(true);
	deactivate.setCodeBlock(block -> {
	    block.addLine("active = false;");
	    block.addLine("// TODO: sendDeactivationEmail(email);");
	});
	// ----------------------------------------------------------------------------------
	boolean clean = false;
	bundle.generateCode(clean);
	// ----------------------------------------------------------------------------------
    }

    @Test
    void test02() throws Exception {
	// ----------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	bundle.setShowProgressbar(true);
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
	MInterface intf = pckg.newInterface("NamedBean");
	intf.addMethod("getName", String.class);
	intf.addMethod("getBean", intf);
	// ----------------------------------------------------------------------------------
	List<MClass> classes = new ArrayList<>();
	for (int i = 0; i < 10; i++) {
	    MClass cls = pckg.newClass("Bean0" + i);
	    cls.addInterface(intf);
	    classes.add(cls);
	}
	int i = 9;
	for (MClass cls : classes) {
	    MField name = cls.addField(String.class, "name").setFinal(true);
	    name.addGetterMethod().setFinal(true).overrides();
	    cls.addFinalFieldsConstructor();
	    MField field = cls.addField(classes.get(i), "bean");
	    field.addAccessorMethods();
	    i = i - 1;
	}
	// ----------------------------------------------------------------------------------
	boolean clean = false;
	bundle.generateCode(clean);
	// ----------------------------------------------------------------------------------
    }

    @Test
    void test03() throws Exception {
	// ----------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
	MInterface intf = pckg.newInterface("Supplier").setGeneric("<T>");
	intf.addMethod("get", "T");
	// ----------------------------------------------------------------------------------
	intf.addAnnotation(new FunctionalInterface() {

	    @Override
	    public Class<? extends Annotation> annotationType() {
		return FunctionalInterface.class;
	    }
	});
	// ----------------------------------------------------------------------------------
	boolean clean = false;
	bundle.generateCode(clean);
	// ----------------------------------------------------------------------------------
    }

    @Test
    void test04() throws Exception {
	// ----------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.main");
	// ----------------------------------------------------------------------------------
	MEnum enm = pckg.newEnum("State");
	enm.addValue("READY");
	enm.addValue("RUNNING");
	enm.addValue("FAILED");
	enm.addValue("SECCEED");
	MClass dis1 = enm.newDispatcher();
	// ----------------------------------------------------------------------------------
	MClass dis2 = pckg.newEnumDispatcher(MClassModifier.class, "ClassModifierDispatcher");
	dis2.addInterface(MEnumDispatcher.class, "<MClassModifier, R>");
	// ----------------------------------------------------------------------------------
	boolean clean = false;
	bundle.generateCode(clean);
	// ----------------------------------------------------------------------------------
    }
}
