package com.cc.jcg.bean;

public interface MBFieldSetter<BT, VT>
	extends MBFieldDef<BT> {

    Class<VT> getType();

    void setValue(BT bean, VT value);
}
