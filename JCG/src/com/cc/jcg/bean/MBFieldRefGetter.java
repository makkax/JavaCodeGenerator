package com.cc.jcg.bean;

public interface MBFieldRefGetter<BT, VT>
	extends MBFieldGetter<BT, VT>, MBRef<MBBeanMeta<VT>> {

    @Override
    VT getValue(BT bean);
}
