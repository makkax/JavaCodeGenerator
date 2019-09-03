package com.cc.jcg.bean;

import java.util.Collection;

public interface MBFieldCollectionSetter<BT, VT>
	extends MBFieldSetter<BT, Collection<VT>> {

    @Override
    Class<Collection<VT>> getType();

    @Override
    void setValue(BT bean, Collection<VT> values);
}
