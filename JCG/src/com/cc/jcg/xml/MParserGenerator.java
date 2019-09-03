package com.cc.jcg.xml;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.cc.jcg.MClass;
import com.cc.jcg.MCodeBlock;
import com.cc.jcg.MCodeGenerator;
import com.cc.jcg.MField;
import com.cc.jcg.MFunctions;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;
import com.cc.jcg.MParameter;
import com.cc.jcg.tools.XmlVisitorReflection;

public class MParserGenerator {

    private final MPackage pckg;
    private transient MClass visitorType;

    public MParserGenerator(MPackage pckg) {
	super();
	this.pckg = pckg;
    }

    protected String getParserName(XmlDocument document) {
	return MFunctions.camelize(document.getRoot().getName().concat("Parser"));
    }

    protected String getAttributeFieldName(XmlAttribute attribute) {
	return "attribute".concat(MFunctions.camelize(attribute.getName()));
    }

    public Set<MClass> getMClasses(XmlDocument document) throws ClassNotFoundException {
	Set<MClass> types = new HashSet<MClass>();
	final String name = getParserName(document);
	final Map<XmlItem, MClass> beans = new HashMap<XmlItem, MClass>();
	XmlDocumentVisitor visitor = new XmlDocumentVisitor() {

	    @Override
	    public void on(final XmlRootNode node) {
		final MClass bean = addBean(node);
		MClass type = pckg.newClass(name).setAbstract(false).makePublic();
		MMethod parse = type.addMethod("parse", void.class, new MParameter(File.class, "file")).makePublic().setFinal(true).setSynchronized(true);
		parse.setBlockContent("new Visitor(file).call();").throwsException(Exception.class);
		type.addField(bean, "root").makePrivate().addGetterMethod().setFinal(true);
		visitorType = type.newInnerClass("Visitor").makePrivate().setFinal(true);
		visitorType.setSuperclass(XmlVisitorReflection.class);
		visitorType.addConstructor(new MParameter(File.class, "file")).setBlockContent("super(file);").throwsException(Exception.class).makePrivate();
		visitorType.addAnnotation(new SuppressWarnings() {

		    @Override
		    public Class<? extends Annotation> annotationType() {
			return SuppressWarnings.class;
		    }

		    @Override
		    public String[] value() {
			return new String[] { "unused" };
		    }
		});
		MMethod on = visitorType.addMethod("on" + bean.getName(), void.class, new MParameter(Element.class, "element"));
		on.makeDefault();
		on.setSynchronized(true);
		on.setGenerator(new MCodeGenerator<MMethod>() {

		    @Override
		    public MCodeBlock getCodeBlock(MMethod element) {
			MCodeBlock block = element.getCodeBlock(element);
			StringBuffer attributes = getAttributes(node);
			block.addLine("root = new " + bean.getName() + "(" + attributes.toString() + ");");
			return block;
		    }
		});
	    }

	    @Override
	    public void on(int level, XmlNode node) {
		System.out.println("+ " + level + " - " + node);
		MClass bean = addBean(node);
		if (node.hasTextContent()) {
		    try {
			bean.addField(String.class, "text").makePrivate().setFinal(false).addAccessorMethods().setter().makeDefault();
		    } catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		    }
		}
	    }

	    private MClass addBean(XmlItem node) {
		String beanName = MFunctions.camelize(node.getName());
		MClass bean = pckg.newClass(beanName);
		bean.setFinal(true);
		for (XmlAttribute a : node.getAttributes()) {
		    String n = getAttributeFieldName(a);
		    n = n.replace("package", "pckg");
		    bean.addField(a.getType().getJavaType(), n).setFinal(true).makePrivate().addGetterMethod().makePublic();
		}
		beans.put(node, bean);
		return bean;
	    }
	};
	document.visit(visitor);
	for (final XmlItem node : beans.keySet()) {
	    final MClass bean = beans.get(node);
	    for (XmlNode child : node.getChildren(XmlCardinality.ONE)) {
		MClass cBean = beans.get(child);
		MField field = bean.addField(cBean, MFunctions.camelize(child.getName())).setFinal(false).makePrivate();
		field.addAccessorMethods().setter().makeDefault();
	    }
	    bean.addFinalFieldsConstructor().makeDefault();
	    for (XmlNode child : node.getChildren(XmlCardinality.MANY)) {
		MClass cBean = beans.get(child);
		bean.addExtraImport(cBean);
		MField field = bean.addField(Set.class, MFunctions.plural(MFunctions.camelize(child.getName()))).setFinal(true).makePrivate();
		bean.addExtraImport(HashSet.class);
		field.setValue("new HashSet<" + cBean.getName() + ">()");
		field.setGeneric("<" + cBean.getName() + ">");
		field.addGetterMethod().makePublic();
	    }
	    if (!node.isRoot()) {
		XmlItem parent = ((XmlNode) node).getParent();
		final MClass parentBean = beans.get(parent);
		final String parentName = parent.isRoot() ? "root" : parentBean.getName();
		final boolean one = parent.getChildren(XmlCardinality.ONE).contains(node);
		visitorType.addField(bean, bean.getName()).makePrivate();
		MMethod on = visitorType.addMethod("on" + bean.getName(), void.class, new MParameter(Element.class, "element"));
		on.makeDefault();
		on.setSynchronized(true);
		on.setGenerator(new MCodeGenerator<MMethod>() {

		    @Override
		    public MCodeBlock getCodeBlock(MMethod element) {
			MCodeBlock block = element.getCodeBlock(element);
			StringBuffer attributes = getAttributes(node);
			block.addLine(bean.getName() + " = new " + bean.getName() + "(" + attributes.toString() + ");");
			if (((XmlNode) node).hasTextContent()) {
			    block.addLine(bean.getName() + ".setText(element.getTextContent());");
			}
			if (one) {
			    block.addLine(parentName + ".set" + bean.getName() + "(" + bean.getName() + ");");
			} else {
			    block.addLine(parentName + ".get" + bean.getName() + "s().add(" + bean.getName() + ");");
			}
			return block;
		    }
		});
		visitorType.addExtraImport(XmlType.class);
	    }
	}
	return types;
    }

    private StringBuffer getAttributes(final XmlItem node) {
	StringBuffer attributes = new StringBuffer();
	for (XmlAttribute a : node.getAttributes()) {
	    attributes.append(a.getType().getMapCall("element.getAttribute(\"" + a.getName() + "\")"));
	    attributes.append(", ");
	}
	if (attributes.length() > 0) {
	    attributes.delete(attributes.length() - 2, attributes.length());
	}
	return attributes;
    }
}
