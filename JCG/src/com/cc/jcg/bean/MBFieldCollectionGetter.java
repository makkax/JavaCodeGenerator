package com.cc.jcg.bean;

import java.util.Collection;

public interface MBFieldCollectionGetter<BT, VT>
	extends MBFieldGetter<BT, Collection<VT>> {

    @Override
    Class<Collection<VT>> getType();

    @Override
    Collection<VT> getValue(BT bean);
}
