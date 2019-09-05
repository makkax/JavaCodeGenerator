package com.cc.jcg.test;

import java.io.File;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.cc.jcg.MBundle;
import com.cc.jcg.MClass;
import com.cc.jcg.MPackage;

public class ReflectionTest {

    public static class SomeClass {

	private final String name;
	private final LocalDate date;
	private final SomeClass other;

	public SomeClass(String name, LocalDate date, SomeClass other) {
	    super();
	    this.name = name;
	    this.date = date;
	    this.other = other;
	}

	public SomeClass(String name, LocalDate date) {
	    super();
	    this.name = name;
	    this.date = date;
	    this.other = null;
	}
    }

    // ----------------------------------------------------------------------------------------------------------------
    // compile the source file with the -parameters option to the javac compiler
    // ----------------------------------------------------------------------------------------------------------------
    @Test
    public void testParameterNames() throws Exception {
	// ----------------------------------------------------------------------------------------------------------------
	MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);
	MBundle.GENERATE_READONLY.set(false);
	// ----------------------------------------------------------------------------------------------------------------
	MBundle bundle = new MBundle(new File("src-generated"));
	MPackage pckg = bundle.newPackage("com.cc.jcg.reflection");
	// ----------------------------------------------------------------------------------------------------------------
	MClass extension = pckg.newClass("SomeClassExtension");
	extension.setSuperclass(SomeClass.class);
	extension.generateConstructors(SomeClass.class);
	// ----------------------------------------------------------------------------------------------------------------
	boolean clean = false;
	bundle.generateCode(clean);
	// ----------------------------------------------------------------------------------------------------------------
    }
}
