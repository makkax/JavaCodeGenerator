package com.cc.jcg.bean;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.cc.jcg.bean.MBFieldBase.MBFieldCollectionBase;
import com.cc.jcg.bean.MBFieldBase.MBFieldRefBase;
import com.cc.jcg.bean.MBFieldBase.MBFieldRefCollectionBase;
import com.cc.jcg.hierarchy.Hierarchy;
import com.cc.jcg.hierarchy.HierarchyBuilder;
import com.cc.jcg.hierarchy.HierarchyImpl;

public abstract class MBBeanMetaBase<BT>
	implements MBBeanMeta<BT> {

    protected static final <MB extends MBBeanMeta> MB getBeanMeta(String key) {
	return (MB) MBRegistry.getBeanMeta(key);
    }

    protected static final MBBeanMeta registerBeanMeta(MBBeanMeta meta) {
	return MBRegistry.registerBeanMeta(meta);
    }

    private final Class<BT> beanType;
    private final Set<MBFieldDef<BT>> fieldDefs;

    protected MBBeanMetaBase(Class<BT> beanType) {
	super();
	this.beanType = beanType;
	fieldDefs = new HashSet<MBFieldDef<BT>>();
	registerBeanMeta(this);
    }

    @Override
    public final Class<BT> getBeanType() {
	return beanType;
    }

    protected final void addFieldMeta(MBFieldDef<BT> field) {
	fieldDefs.add(field);
    }

    @Override
    public final Set<MBFieldDef<BT>> getFieldDefs() {
	return Collections.unmodifiableSet(fieldDefs);
    }

    @Override
    public final Set<MBField<BT, ?>> getFields() {
	Set<MBField<BT, ?>> fields = new HashSet<MBField<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBField) {
		fields.add((MBField<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public final Set<MBFieldGetter<BT, ?>> getFieldGetters() {
	Set<MBFieldGetter<BT, ?>> fields = new HashSet<MBFieldGetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldGetter) {
		fields.add((MBFieldGetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public final Set<MBFieldSetter<BT, ?>> getFieldSetters() {
	Set<MBFieldSetter<BT, ?>> fields = new HashSet<MBFieldSetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldSetter) {
		fields.add((MBFieldSetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRef<BT, ?>> getFieldRefs() {
	Set<MBFieldRef<BT, ?>> fields = new HashSet<MBFieldRef<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRef) {
		fields.add((MBFieldRef<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRefGetter<BT, ?>> getFieldRefGetters() {
	Set<MBFieldRefGetter<BT, ?>> fields = new HashSet<MBFieldRefGetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRefGetter) {
		fields.add((MBFieldRefGetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRefSetter<BT, ?>> getFieldRefSetters() {
	Set<MBFieldRefSetter<BT, ?>> fields = new HashSet<MBFieldRefSetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRefSetter) {
		fields.add((MBFieldRefSetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldCollection<BT, ?>> getFieldCollections() {
	Set<MBFieldCollection<BT, ?>> fields = new HashSet<MBFieldCollection<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldCollection) {
		fields.add((MBFieldCollection<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldCollectionGetter<BT, ?>> getFieldCollectionGetters() {
	Set<MBFieldCollectionGetter<BT, ?>> fields = new HashSet<MBFieldCollectionGetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldCollectionGetter) {
		fields.add((MBFieldCollectionGetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldCollectionSetter<BT, ?>> getFieldCollectionSetters() {
	Set<MBFieldCollectionSetter<BT, ?>> fields = new HashSet<MBFieldCollectionSetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldCollectionSetter) {
		fields.add((MBFieldCollectionSetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRefCollection<BT, ?>> getFieldRefCollections() {
	Set<MBFieldRefCollection<BT, ?>> fields = new HashSet<MBFieldRefCollection<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRefCollection) {
		fields.add((MBFieldRefCollection<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRefCollectionGetter<BT, ?>> getFieldRefCollectionGetters() {
	Set<MBFieldRefCollectionGetter<BT, ?>> fields = new HashSet<MBFieldRefCollectionGetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRefCollectionGetter) {
		fields.add((MBFieldRefCollectionGetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    @Override
    public Set<MBFieldRefCollectionSetter<BT, ?>> getFieldRefCollectionSetters() {
	Set<MBFieldRefCollectionSetter<BT, ?>> fields = new HashSet<MBFieldRefCollectionSetter<BT, ?>>();
	for (MBFieldDef<BT> field : fieldDefs) {
	    if (field instanceof MBFieldRefCollectionSetter) {
		fields.add((MBFieldRefCollectionSetter<BT, ?>) field);
	    }
	}
	return Collections.unmodifiableSet(fields);
    }

    protected final <VT> MBField<BT, VT> newField(Class<VT> type, String name) {
	return new MBFieldBase<BT, VT>(beanType, type, name);
    }

    protected final <VT> MBFieldSetter<BT, VT> newFieldSetter(Class<VT> type, String name) {
	return new MBFieldBase<BT, VT>(beanType, type, name);
    }

    protected final <VT> MBFieldGetter<BT, VT> newFieldGetter(Class<VT> type, String name) {
	return new MBFieldBase<BT, VT>(beanType, type, name);
    }

    protected final <VT> MBFieldCollection<BT, VT> newFieldCollection(Class<VT> type, String name) {
	return new MBFieldCollectionBase(beanType, type, name);
    }

    protected final <VT> MBFieldRef<BT, VT> newFieldRef(Class<VT> type, String name) {
	return new MBFieldRefBase<BT, VT>(beanType, type, name);
    }

    protected final <VT> MBFieldRefCollection<BT, VT> newFieldRefCollection(Class<VT> type, String name) {
	return new MBFieldRefCollectionBase(beanType, type, name);
    }

    @Override
    public void visit(MBFieldListener<BT> listener) {
	for (MBFieldDef<BT> fm : fieldDefs) {
	    listener.on(fm);
	    if (fm instanceof MBField) {
		listener.on((MBField<BT, ?>) fm);
	    }
	    if (fm instanceof MBFieldCollection) {
		listener.on((MBFieldCollection<BT, ?>) fm);
	    }
	    if (fm instanceof MBFieldRef) {
		listener.on((MBFieldRef<BT, ?>) fm);
	    }
	}
    }

    @Override
    public Hierarchy<MBFieldRef<?, ?>> getRefsHierarchy(MBFieldRef<BT, ?> root) {
	HierarchyBuilder<MBFieldRef<?, ?>> traverser = new HierarchyBuilder<MBFieldRef<?, ?>>() {

	    @Override
	    public Collection<MBFieldRef<?, ?>> getChildren(MBFieldRef<?, ?> item) {
		MBBeanMeta<?> meta = item.getRef();
		Set<MBFieldRef<?, ?>> refs = (Set<MBFieldRef<?, ?>>) (Set<?>) meta.getFieldRefs();
		return refs;
	    }
	};
	Hierarchy<MBFieldRef<?, ?>> hierarchy = new HierarchyImpl<MBFieldRef<?, ?>>(traverser, root);
	return hierarchy;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("MBBeanMetaBase [beanType=");
	builder.append(beanType);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (beanType == null ? 0 : beanType.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	MBBeanMetaBase other = (MBBeanMetaBase) obj;
	if (beanType == null) {
	    if (other.beanType != null) {
		return false;
	    }
	} else if (!beanType.equals(other.beanType)) {
	    return false;
	}
	return true;
    }
}
