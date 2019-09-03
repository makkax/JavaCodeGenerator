package com.cc.jcg.bean;

import java.util.Collection;

public interface MBFieldCollection<BT, VT>
	extends MBFieldCollectionGetter<BT, VT>, MBFieldCollectionSetter<BT, VT> {

    @Override
    Class<Collection<VT>> getType();

    @Override
    Collection<VT> getValue(BT bean);

    @Override
    void setValue(BT bean, Collection<VT> value);
}