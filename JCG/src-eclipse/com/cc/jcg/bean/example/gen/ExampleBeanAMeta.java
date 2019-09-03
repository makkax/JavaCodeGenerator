package com.cc.jcg.bean.example.gen;

import com.cc.jcg.MGenerated;
import com.cc.jcg.bean.MBBeanMetaBase;
import com.cc.jcg.bean.MBField;
import com.cc.jcg.bean.MBFieldCollection;
import com.cc.jcg.bean.MBFieldRef;
import com.cc.jcg.bean.MBFieldRefCollection;
import com.cc.jcg.bean.example.ExampleBeanA;
import com.cc.jcg.bean.example.ExampleBeanB;
import java.util.Date;

@MGenerated
public final class ExampleBeanAMeta
        extends MBBeanMetaBase<ExampleBeanA> {
    
    public static ExampleBeanAMeta getInstance() {
        if (getBeanMeta("com.cc.jcg.bean.example.gen.ExampleBeanAMeta") == null) {
            registerBeanMeta(new ExampleBeanAMeta());
        }
        return getBeanMeta("com.cc.jcg.bean.example.gen.ExampleBeanAMeta");
    }
    
    public final MBField<ExampleBeanA, String> name = newField(String.class, "name");
    public final MBField<ExampleBeanA, Date> created = newField(Date.class, "created");
    public final MBField<ExampleBeanA, Integer> age = newField(Integer.class, "age");
    public final MBField<ExampleBeanA, Boolean> manager = newField(Boolean.class, "manager");
    public final MBFieldCollection<ExampleBeanA, String> values = newFieldCollection(String.class, "values");
    public final MBFieldRef<ExampleBeanA, ExampleBeanB> ref = newFieldRef(ExampleBeanB.class, "ref");
    public final MBFieldRefCollection<ExampleBeanA, ExampleBeanB> refs = newFieldRefCollection(ExampleBeanB.class, "refs");
    
    private ExampleBeanAMeta() {
        super(ExampleBeanA.class);
        addFieldMeta(name);
        addFieldMeta(created);
        addFieldMeta(age);
        addFieldMeta(manager);
        addFieldMeta(values);
        addFieldMeta(ref);
        addFieldMeta(refs);
    }
}
