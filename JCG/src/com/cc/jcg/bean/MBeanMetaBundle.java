package com.cc.jcg.bean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import com.cc.jcg.MBundle;
import com.cc.jcg.MClass;
import com.cc.jcg.MCodeBlock;
import com.cc.jcg.MCodeGenerator;
import com.cc.jcg.MConstructor;
import com.cc.jcg.MField;
import com.cc.jcg.MMethod;
import com.cc.jcg.MPackage;

public class MBeanMetaBundle
	extends MBundle {

    public static final Map<Class, MBeanMetaBundle> generate(Collection<Class<?>> entityTypes, File targetDir, String pckg) throws Exception {
	HashMap<Class, MBeanMetaBundle> bundles = new HashMap<Class, MBeanMetaBundle>();
	generate(entityTypes, targetDir, pckg, bundles);
	return bundles;
    }

    public static final Map<Class, MBeanMetaBundle> generate(Collection<Class<?>> entityTypes, File targetDir, String pckg, Map<Class, MBeanMetaBundle> bundles) throws Exception {
	for (Class type : entityTypes) {
	    if (isBean(type)) {
		new MBeanMetaBundle(targetDir, pckg, type, bundles).generateCode(false);
	    }
	}
	return bundles;
    }

    public static final void generateMetaRegister(File srcDir, String pckg, String name, final Map<Class, MBeanMetaBundle> bundles) throws Exception {
	final TreeMap<Class, MBeanMetaBundle> sorted = new TreeMap<Class, MBeanMetaBundle>(new Comparator<Class>() {

	    @Override
	    public int compare(Class o1, Class o2) {
		return o1.getName().compareTo(o2.getName());
	    }
	});
	sorted.putAll(bundles);
	MPackage bundle = new MBundle(srcDir, name).newPackage(pckg);
	final MClass type = bundle.newClass(name);
	type.makePublic().setFinal(true);
	type.addConstructor().makePrivate().setBlockContent("super();");
	for (Class key : sorted.keySet()) {
	    MBeanMetaBundle beanMeta = sorted.get(key);
	    String fieldName = beanMeta.getBeanMeta().getName().concat("Model");
	    MField field = type.addStaticField(beanMeta.getBeanMeta(), fieldName).setFinal(true).makePublic();
	    field.setValue(beanMeta.getBeanMeta().getName().concat(".getInstance();"));
	}
	MMethod register = type.addStaticMethod("register", void.class).makePublic().setFinal(true);
	register.setBlockContent("getAllMetaModels();");
	MMethod getAllMetaModels = type.addStaticMethod("getAllMetaModels", "Collection<MBBeanMeta>").makePublic().setFinal(true);
	type.addExtraImport(Collection.class);
	type.addExtraImport(MBBeanMeta.class);
	type.addExtraImport(Set.class);
	type.addExtraImport(HashSet.class);
	getAllMetaModels.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("Set all = new HashSet();");
		for (Class key : sorted.keySet()) {
		    MBeanMetaBundle beanMeta = sorted.get(key);
		    String fieldName = beanMeta.getBeanMeta().getName().concat("Model");
		    block.addLine("all.add(" + fieldName + ");");
		}
		block.addLine("return all;");
		return block;
	    }
	});
	bundle.generateCode(false);
    }

    private final MPackage pckg;
    private final Class beanType;
    private final String BT;
    private MClass beanMeta;
    private final Map<Class, MBeanMetaBundle> bundles;

    public MBeanMetaBundle(File dir, String pckg, Class beanType) throws Exception {
	this(dir, pckg, beanType, new HashMap<Class, MBeanMetaBundle>());
    }

    public MBeanMetaBundle(File dir, String pckg, Class beanType, Map<Class, MBeanMetaBundle> bundles) throws Exception {
	super(dir, "BeanMetaBundle");
	this.pckg = newPackage(pckg);
	this.beanType = beanType;
	BT = beanType.getSimpleName();
	this.bundles = bundles;
	bundles.put(beanType, this);
	buildModel();
    }

    public final MClass getBeanMeta() {
	return beanMeta;
    }

    @SuppressWarnings("null")
    private void buildModel() throws Exception {
	beanMeta = pckg.newClass(BT.concat("Meta"));
	beanMeta.setFinal(true);
	beanMeta.makePublic();
	beanMeta.setSuperclass(MBBeanMetaBase.class, BT);
	// -----------------------------------------------------------------------------------------------------------------------
	MMethod getInstance = beanMeta.addStaticMethod("getInstance", beanMeta).makePublic();
	getInstance.setGenerator(new MCodeGenerator<MMethod>() {

	    @Override
	    public MCodeBlock getCodeBlock(MMethod element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("if (getBeanMeta(\"" + beanMeta.getQualifiedName() + "\") == null) {");
		block.addLine(MCodeBlock.tab() + "registerBeanMeta(new " + beanMeta.getName() + "());");
		block.addLine("}");
		block.addLine("return getBeanMeta(\"" + beanMeta.getQualifiedName() + "\");");
		return block;
	    }
	});
	// -----------------------------------------------------------------------------------------------------------------------
	beanMeta.addConstructor().makePrivate().setGenerator(new MCodeGenerator<MConstructor>() {

	    @Override
	    public MCodeBlock getCodeBlock(MConstructor element) {
		MCodeBlock block = element.getCodeBlock(element);
		block.addLine("super(" + BT + ".class);");
		for (Field field : beanType.getDeclaredFields()) {
		    if (!Modifier.isStatic(field.getModifiers())) {
			block.addLine("addFieldMeta(" + field.getName() + ");");
		    }
		}
		return block;
	    }
	});
	// -----------------------------------------------------------------------------------------------------------------------
	for (Field field : beanType.getDeclaredFields()) {
	    if (!Modifier.isStatic(field.getModifiers())) {
		Class fieldType = field.getType();
		if (fieldType.equals(boolean.class)) {
		    fieldType = Boolean.class;
		} else if (fieldType.equals(int.class)) {
		    fieldType = Integer.class;
		} else if (fieldType.equals(long.class)) {
		    fieldType = Long.class;
		} else if (fieldType.equals(double.class)) {
		    fieldType = Double.class;
		} else if (fieldType.equals(float.class)) {
		    fieldType = Float.class;
		} else if (fieldType.equals(short.class)) {
		    fieldType = Short.class;
		}
		boolean isRef = isBean(fieldType);
		boolean isCollection = Collection.class.isAssignableFrom(fieldType);
		boolean isCollectionRef = false;
		boolean hasGetter = hasGetter(beanType, field);
		boolean hasSetter = hasSetter(beanType, field);
		Class<?> genericType = null;
		if (isCollection) {
		    genericType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		    isCollectionRef = isBean(genericType);
		    beanMeta.addExtraImport(genericType);
		} else {
		    beanMeta.addExtraImport(fieldType);
		}
		final MField fm;
		if (isCollectionRef) {
		    if (!bundles.containsKey(genericType)) {
			if (generateMetaForType(genericType)) {
			    MBeanMetaBundle refBundle = new MBeanMetaBundle(getSrcDir(), pckg.getName(), genericType, bundles);
			    // beanMeta.addExtraImport(refBundle.getBeanMeta());
			    bundles.put(genericType, refBundle);
			    addSubBundle(refBundle);
			} else {
			    MBBeanMeta ref = MBRegistry.getBeanMetaByBeanType(genericType);
			    if (ref != null) {
				String refPckg = ref.getClass().getPackage().getName();
				MBeanMetaBundle refBundle = new MBeanMetaBundle(null, refPckg, genericType, bundles);
				// beanMeta.addExtraImport(ref.getClass());
				bundles.put(genericType, refBundle);
			    }
			}
		    }
		    final Class<?> intf;
		    if (hasGetter && hasSetter) {
			intf = MBFieldRefCollection.class;
		    } else if (hasGetter) {
			intf = MBFieldRefCollectionGetter.class;
		    } else if (hasSetter) {
			intf = MBFieldRefCollectionSetter.class;
		    } else {
			intf = null;
		    }
		    if (intf != null) {
			fm = beanMeta.addField(intf, field.getName()).setGeneric("<" + BT + ", " + genericType.getSimpleName() + ">");
			fm.setValue("newFieldRefCollection(" + genericType.getSimpleName() + ".class, \"" + field.getName() + "\")");
			beanMeta.addExtraImport(genericType);
		    } else {
			fm = null;
		    }
		} else if (isRef) {
		    if (!bundles.containsKey(fieldType)) {
			if (generateMetaForType(fieldType)) {
			    MBeanMetaBundle refBundle = new MBeanMetaBundle(getSrcDir(), pckg.getName(), fieldType, bundles);
			    // beanMeta.addExtraImport(refBundle.getBeanMeta());
			    bundles.put(fieldType, refBundle);
			    addSubBundle(refBundle);
			} else {
			    MBBeanMeta ref = MBRegistry.getBeanMetaByBeanType(fieldType);
			    if (ref != null) {
				String refPckg = ref.getClass().getPackage().getName();
				MBeanMetaBundle refBundle = new MBeanMetaBundle(null, refPckg, fieldType, bundles);
				// beanMeta.addExtraImport(ref.getClass());
				bundles.put(fieldType, refBundle);
			    }
			}
		    }
		    final Class<?> intf;
		    if (hasGetter && hasSetter) {
			intf = MBFieldRef.class;
		    } else if (hasGetter) {
			intf = MBFieldRefGetter.class;
		    } else if (hasSetter) {
			intf = MBFieldRefSetter.class;
		    } else {
			intf = null;
		    }
		    if (intf != null) {
			fm = beanMeta.addField(intf, field.getName()).setGeneric("<" + BT + ", " + fieldType.getSimpleName() + ">");
			fm.setValue("newFieldRef(" + fieldType.getSimpleName() + ".class, \"" + field.getName() + "\")");
			beanMeta.addExtraImport(fieldType);
		    } else {
			fm = null;
		    }
		} else if (isCollection) {
		    final Class<?> intf;
		    if (hasGetter && hasSetter) {
			intf = MBFieldCollection.class;
		    } else if (hasGetter) {
			intf = MBFieldCollectionGetter.class;
		    } else if (hasSetter) {
			intf = MBFieldCollectionSetter.class;
		    } else {
			intf = null;
		    }
		    if (intf != null) {
			fm = beanMeta.addField(intf, field.getName()).setGeneric("<" + BT + ", " + genericType.getSimpleName() + ">");
			fm.setValue("newFieldCollection(" + genericType.getSimpleName() + ".class, \"" + field.getName() + "\")");
			beanMeta.addExtraImport(genericType);
		    } else {
			fm = null;
		    }
		} else {
		    final Class<?> intf;
		    final String extra;
		    if (hasGetter && hasSetter) {
			intf = MBField.class;
			extra = "";
		    } else if (hasGetter) {
			intf = MBFieldGetter.class;
			extra = "Getter";
		    } else if (hasSetter) {
			intf = MBFieldSetter.class;
			extra = "Setter";
		    } else {
			intf = null;
			extra = null;
		    }
		    if (intf != null) {
			fm = beanMeta.addField(intf, field.getName()).setGeneric("<" + BT + ", " + fieldType.getSimpleName() + ">");
			fm.setValue("newField" + extra + "(" + fieldType.getSimpleName() + ".class, \"" + field.getName() + "\")");
			beanMeta.addExtraImport(fieldType);
		    } else {
			fm = null;
		    }
		}
		if (fm != null) {
		    fm.makePublic();
		    fm.setFinal(true);
		}
	    }
	}
	// -----------------------------------------------------------------------------------------------------------------------
	beanMeta.addExtraImport(beanType);
    }

    protected boolean hasSetter(Class type, Field field) {
	return MBFieldBase.getSetter(type, field) != null;
    }

    protected boolean hasGetter(Class type, Field field) {
	return MBFieldBase.getGetter(type, field) != null;
    }

    protected boolean generateMetaForType(Class type) {
	return true;
    }

    private static boolean isBean(Class type) {
	return type.isAnnotationPresent(MBBean.class) || type.isAnnotationPresent(Entity.class) || type.isAnnotationPresent(Embeddable.class);
    }
}
