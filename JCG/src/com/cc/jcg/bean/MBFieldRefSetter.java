package com.cc.jcg.bean;

public interface MBFieldRefSetter<BT, VT>
	extends MBFieldSetter<BT, VT>, MBRef<MBBeanMeta<VT>> {

    @Override
    MBBeanMeta<VT> getRef();
}