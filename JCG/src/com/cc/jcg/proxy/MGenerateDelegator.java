package com.cc.jcg.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicInteger;

import com.cc.jcg.MClass;
import com.cc.jcg.MConstructor;
import com.cc.jcg.MField;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;

public class MGenerateDelegator {

    private final MPackage pckg;
    private final Class<?> intf;

    public MGenerateDelegator(MPackage pckg, Class<?> intf) {
	super();
	this.pckg = pckg;
	this.intf = intf;
    }

    public void generate() {
	generate(intf.getSimpleName().concat("Delegator"));
    }

    public void generate(String name) {
	MClass cls = pckg.newClass(name);
	cls.addInterface(intf);
	MField object = cls.addField(intf, "object").setFinal(true);
	MConstructor cst = cls.addFinalFieldsConstructor().makePublic();
	AtomicInteger counter = new AtomicInteger(0);
	for (Method method : intf.getDeclaredMethods()) {
	    if (!Modifier.isStatic(method.getModifiers())) {
		MMethod mmethod = cls.addMethod(method);
		mmethod.overrides();
		final int i = counter.incrementAndGet();
		StringBuffer s = new StringBuffer();
		String pars = mmethod.getParameterNames();
		if (method.getReturnType().equals(void.class)) {
		    s.append("object." + mmethod.getName() + "(" + pars + ");\n");
		} else {
		    s.append("return object." + mmethod.getName() + "(" + pars + ");\n");
		    cls.addImport(method.getReturnType());
		}
		mmethod.setBlockContent(s);
	    }
	}
    }
}
