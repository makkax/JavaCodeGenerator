package com.cc.jcg.bean;

public interface MBFieldGetter<BT, VT>
	extends MBFieldDef<BT> {

    Class<VT> getType();

    VT getValue(BT bean);
}
