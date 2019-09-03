package com.cc.jcg.bean.example.gen;

import com.cc.jcg.MGenerated;
import com.cc.jcg.bean.MBBeanMetaBase;
import com.cc.jcg.bean.MBField;
import com.cc.jcg.bean.MBFieldCollection;
import com.cc.jcg.bean.example.ExampleBeanB;
import com.cc.jcg.bean.example.PrototypeBeanMeta;
import java.util.Calendar;

@MGenerated
public final class ExampleBeanBMeta
        extends MBBeanMetaBase<ExampleBeanB> {
    
    public static ExampleBeanBMeta getInstance() {
        if (getBeanMeta("com.cc.jcg.bean.example.gen.ExampleBeanBMeta") == null) {
            registerBeanMeta(new ExampleBeanBMeta());
        }
        return getBeanMeta("com.cc.jcg.bean.example.gen.ExampleBeanBMeta");
    }
    
    public final MBField<ExampleBeanB, String> name = newField(String.class, "name");
    public final MBField<ExampleBeanB, Calendar> calendar = newField(Calendar.class, "calendar");
    public final MBField<ExampleBeanB, PrototypeBeanMeta> back = newField(PrototypeBeanMeta.class, "back");
    public final MBFieldCollection<ExampleBeanB, PrototypeBeanMeta> backs = newFieldCollection(PrototypeBeanMeta.class, "backs");
    
    private ExampleBeanBMeta() {
        super(ExampleBeanB.class);
        addFieldMeta(name);
        addFieldMeta(calendar);
        addFieldMeta(back);
        addFieldMeta(backs);
    }
}
