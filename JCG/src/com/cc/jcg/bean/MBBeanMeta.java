package com.cc.jcg.bean;

import java.util.Set;

import com.cc.jcg.hierarchy.Hierarchy;

public interface MBBeanMeta<BT> {

    Class<BT> getBeanType();

    Set<MBFieldDef<BT>> getFieldDefs();

    Set<MBField<BT, ?>> getFields();

    Set<MBFieldGetter<BT, ?>> getFieldGetters();

    Set<MBFieldSetter<BT, ?>> getFieldSetters();

    Set<MBFieldRef<BT, ?>> getFieldRefs();

    Set<MBFieldRefGetter<BT, ?>> getFieldRefGetters();

    Set<MBFieldRefSetter<BT, ?>> getFieldRefSetters();

    Set<MBFieldCollection<BT, ?>> getFieldCollections();

    Set<MBFieldCollectionGetter<BT, ?>> getFieldCollectionGetters();

    Set<MBFieldCollectionSetter<BT, ?>> getFieldCollectionSetters();

    Set<MBFieldRefCollection<BT, ?>> getFieldRefCollections();

    Set<MBFieldRefCollectionGetter<BT, ?>> getFieldRefCollectionGetters();

    Set<MBFieldRefCollectionSetter<BT, ?>> getFieldRefCollectionSetters();

    void visit(MBFieldListener<BT> listener);

    Hierarchy<MBFieldRef<?, ?>> getRefsHierarchy(MBFieldRef<BT, ?> root);
}
