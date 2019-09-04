package com.cc.jcg.test;

import java.io.File;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MClass;
import com.cc.jcg.MCodeBlock;
import com.cc.jcg.MCodeGenerator;
import com.cc.jcg.MEnum;
import com.cc.jcg.MField;
import com.cc.jcg.MInterface;
import com.cc.jcg.MJavaFile;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.cc.jcg.MType;

// @Table
public class ExamplesTest {

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Test
    public void test() throws Exception {
	// 1
	final MBundle bundle = new MBundle(new File("src-generated"), "MyBundle");
	final MPackage pckg = bundle.newPackage("com.cc.jcg.examples2");
	final MClass cls = pckg.newClass("Example");
	final MClass scls = cls.newSubclass("SubclassExample");
	final MInterface intf = pckg.newInterface("IExample");
	final MEnum enm = pckg.newEnum("MyEnum");
	// Annotations
	pckg.newAnnotation("Simple", ElementType.FIELD, RetentionPolicy.SOURCE);
	// 1b
	// cls.addAnnotation(new Table() {
	//
	// @Override
	// public Class<? extends Annotation> annotationType() {
	// return Table.class;
	// }
	//
	// @Override
	// public String catalog() {
	// return "Catalog";
	// }
	//
	// @Override
	// public String name() {
	// return "Name";
	// }
	//
	// @Override
	// public String schema() {
	// return null;
	// }
	//
	// @Override
	// public UniqueConstraint[] uniqueConstraints() {
	// return null;
	// }
	// });
	// 2
	final MField fld = cls.addField(String.class, "name").makePrivateFinal();
	cls.addFinalFieldsConstructor().makeProtected();
	scls.overrideConstructors();
	fld.addGetterMethod().setFinal(true);
	final MMethod m1 = cls.addMethod("getSimpleMethod", void.class);
	final MMethod m2 = cls.addMethod("getSimpleMethod2", void.class, new MParameter(String.class, "parameter"));
	final MMethod m3 = cls.addMethod("getSimpleMethod3", void.class, new MParameter(enm, "enumParameter"));
	final MMethod m4 = cls.addMethod("getSimpleMethod4", scls, new MParameter(enm, "enumParameter")).returnNull();
	// JavaFile
	pckg.addJavaFiles(new File("src-generated/com/cc/jcg/examples"));
	final MType javaFile = pckg.addJavaFile(new File("src-eclipse/com/cc/jcg/test/types/HashAndEquals.java"), "EqualsAndHash");
	pckg.addJavaFile(new File("src-eclipse/com/cc/jcg/test/types/RefType.java"), "Santorini");
	final MMethod m5 = cls.addMethod("getJavaFile", javaFile, new MParameter(javaFile, "file")).returnNull();
	final MJavaFile javaFileGeneric = pckg.addJavaFile(new File("src-eclipse/com/cc/jcg/test/types/TypeWithGeneric.java"));
	javaFileGeneric.getReplacements().put("Integer", "BigDecimal");
	javaFileGeneric.addImport(BigDecimal.class);
	// 3
	cls.addExtraImport(BigDecimal.class);
	cls.addExtraImport(intf);
	// 4
	cls.addExtraImport(Serializable.class);
	cls.setGeneric("<T extends Serializable>");
	cls.addField(List.class, "list").setGeneric("<T>");
	cls.addMethod("getList", List.class).setGenericReturnType("<T>").returnNull();
	// 5
	System.getProperty("hey!");
	m1.addContentRow("System.getProperty(\"hey!\");");
	m2.setBlockContent(new StringBuffer("System.getProperty(\"hey!\");"));
	m3.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		final MCodeBlock block = element.getCodeBlock(element);
		block.addLine("// first line");
		block.addEmptyLine();
		block.addLine("System.getProperty(\"hey!\");");
		return block;
	    }
	});
	// 6
	final List<MParameter> fields = new ArrayList<MParameter>();
	fields.add(new MParameter(String.class, "name"));
	fields.add(new MParameter(enm, "type"));
	final MClass bean = pckg.newBean("MyBean", fields);
	bean.removeAllConstructors();
	bean.addConstructor(fields).makeDefault();
	// 7
	final MClass delegate = pckg.newDelegatorClass(bean);
	delegate.setTemplate(true);
	pckg.newTypeSafeEnum("SpecialEnum", Arrays.asList("READY", "DRAFT", "CANCELLED", "CLOSED")).setTemplate(true);
	// generateCode
	bundle.generateCode(false);
    }
}
