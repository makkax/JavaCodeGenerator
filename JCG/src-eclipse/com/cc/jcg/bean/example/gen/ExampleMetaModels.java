package com.cc.jcg.bean.example.gen;

import com.cc.jcg.MGenerated;
import com.cc.jcg.bean.MBBeanMeta;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@MGenerated
public final class ExampleMetaModels {
    
    public static final ExampleBeanAMeta ExampleBeanAMetaModel = ExampleBeanAMeta.getInstance();
    public static final ExampleBeanBMeta ExampleBeanBMetaModel = ExampleBeanBMeta.getInstance();
    
    public static void register() {
        getAllMetaModels();
    }
    
    public static Collection<MBBeanMeta> getAllMetaModels() {
        Set all = new HashSet();
        all.add(ExampleBeanAMetaModel);
        all.add(ExampleBeanBMetaModel);
        return all;
    }
    
    private ExampleMetaModels() {
        super();
    }
}
