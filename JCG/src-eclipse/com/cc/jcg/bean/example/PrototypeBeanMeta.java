package com.cc.jcg.bean.example;

import java.util.Date;

import com.cc.jcg.bean.MBBeanMetaBase;
import com.cc.jcg.bean.MBField;
import com.cc.jcg.bean.MBFieldCollection;
import com.cc.jcg.bean.MBFieldCollectionGetter;
import com.cc.jcg.bean.MBFieldCollectionSetter;
import com.cc.jcg.bean.MBFieldDef;
import com.cc.jcg.bean.MBFieldGetter;
import com.cc.jcg.bean.MBFieldRef;
import com.cc.jcg.bean.MBFieldRefCollection;
import com.cc.jcg.bean.MBFieldRefCollectionGetter;
import com.cc.jcg.bean.MBFieldRefCollectionSetter;
import com.cc.jcg.bean.MBFieldRefGetter;
import com.cc.jcg.bean.MBFieldRefSetter;
import com.cc.jcg.bean.MBFieldSetter;

public final class PrototypeBeanMeta
	extends MBBeanMetaBase<ExampleBeanA> {

    public final PrototypeBeanMeta getInstance() {
	if (getBeanMeta("PrototypeBeanMeta") == null) {
	    registerBeanMeta(new PrototypeBeanMeta());
	}
	return getBeanMeta("PrototypeBeanMeta");
    }

    public final MBFieldDef<ExampleBeanA> name = newField(String.class, "name");
    public final MBField<ExampleBeanA, Date> created = newField(Date.class, "created");
    public final MBFieldSetter<ExampleBeanA, Integer> age = newFieldSetter(Integer.class, "age");
    public final MBFieldGetter<ExampleBeanA, Boolean> manager = newFieldGetter(Boolean.class, "manager");
    public final MBFieldCollection<ExampleBeanA, String> values = newFieldCollection(String.class, "values");
    public final MBFieldCollectionGetter<ExampleBeanA, String> values02 = newFieldCollection(String.class, "values");
    public final MBFieldCollectionSetter<ExampleBeanA, String> values03 = newFieldCollection(String.class, "values");
    public final MBFieldRef<ExampleBeanA, ExampleBeanB> ref = newFieldRef(ExampleBeanB.class, "ref");
    public final MBFieldRefGetter<ExampleBeanA, ExampleBeanB> ref02 = newFieldRef(ExampleBeanB.class, "ref");
    public final MBFieldRefSetter<ExampleBeanA, ExampleBeanB> ref03 = newFieldRef(ExampleBeanB.class, "ref");
    public final MBFieldRefCollection<ExampleBeanA, ExampleBeanB> refs = newFieldRefCollection(ExampleBeanB.class, "refs");
    public final MBFieldRefCollectionGetter<ExampleBeanA, ExampleBeanB> refs02 = newFieldRefCollection(ExampleBeanB.class, "refs");
    public final MBFieldRefCollectionSetter<ExampleBeanA, ExampleBeanB> refs03 = newFieldRefCollection(ExampleBeanB.class, "refs");

    private PrototypeBeanMeta() {
	super(ExampleBeanA.class);
	addFieldMeta(name);
	addFieldMeta(created);
	addFieldMeta(age);
	addFieldMeta(manager);
	addFieldMeta(values);
	addFieldMeta(ref);
    }
}
