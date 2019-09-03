package com.cc.jcg.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.atomic.AtomicInteger;

import com.cc.jcg.MClass;
import com.cc.jcg.MConstructor;
import com.cc.jcg.MField;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;

public class MGenerateMethodsProxy {

    private final MPackage pckg;
    private final Class<?> intf;

    public MGenerateMethodsProxy(MPackage pckg, Class<?> intf) {
	super();
	this.pckg = pckg;
	this.intf = intf;
    }

    public void generate() {
	generate(intf.getSimpleName().concat("MethodsProxy"));
    }

    public void generate(String name) {
	MClass cls = pckg.newClass(name);
	cls.addInterface(intf);
	MField object = cls.addField(intf, "object").setFinal(true);
	MField proxy = cls.addField(Proxy.class, "proxy").setGeneric(intf).setFinal(true);
	MConstructor cst = cls.addFinalFieldsConstructor().makePublic();
	AtomicInteger counter = new AtomicInteger(0);
	for (Method method : intf.getDeclaredMethods()) {
	    if (!Modifier.isStatic(method.getModifiers())) {
		cls.addImport(ProxyMethod.class);
		MMethod mmethod = cls.addMethod(method);
		mmethod.overrides();
		final int i = counter.incrementAndGet();
		mmethod.addAnnotation(new PM() {

		    @Override
		    public Class<? extends Annotation> annotationType() {
			return PM.class;
		    }

		    @Override
		    public int value() {
			return i;
		    }
		});
		StringBuffer s = new StringBuffer();
		s.append("ProxyMethod method = new ProxyMethod(" + cls.getName() + ".class, " + i + ");\n");
		if (mmethod.getParameters().size() > 0) {
		    s.append("// parameters\n");
		    for (MParameter p : mmethod.getParameters()) {
			s.append("method.getParameter(\"" + p.getName() + "\").setValue(" + p.getName() + ");\n");
		    }
		} else {
		    s.append("// no parameters\n");
		}
		String pars = mmethod.getParameterNames();
		if (method.getReturnType().equals(void.class)) {
		    s.append("// no return value\n");
		    s.append("object." + mmethod.getName() + "(" + pars + ");\n");
		    s.append("proxy.onMethod(object, method);\n");
		} else {
		    s.append("// return value\n");
		    String rt = method.getReturnType().getSimpleName();
		    if (method.getGenericReturnType() instanceof ParameterizedType) {
			String generic = ((ParameterizedType) method.getGenericReturnType()).toString();
			generic = generic.substring(generic.indexOf("<"));
			rt = rt.concat(generic);
		    }
		    s.append(rt + " value = object." + mmethod.getName() + "(" + pars + ");\n");
		    s.append("method.setValue(value);\n");
		    s.append("proxy.onMethod(object, method);\n");
		    s.append("return value;\n");
		    cls.addImport(method.getReturnType());
		}
		mmethod.setBlockContent(s);
	    }
	}
    }
}
