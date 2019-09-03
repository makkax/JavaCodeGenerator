package com.cc.jcg.bean;

import java.util.Collection;

public interface MBFieldRefCollectionSetter<BT, VT>
	extends MBFieldCollectionSetter<BT, VT>, MBRef<MBBeanMeta<VT>> {

    @Override
    void setValue(BT bean, Collection<VT> values);
}
