package com.cc.jcg.bean;

public abstract class MBFieldListenerBase<BT>
	implements MBFieldListener<BT> {

    @Override
    public void on(MBFieldDef<BT> fm) {
	// do nothing by default
    }

    @Override
    public void on(MBField<BT, ?> fm) {
	// do nothing by default
    }

    @Override
    public void on(MBFieldCollection<BT, ?> fm) {
	// do nothing by default
    }

    @Override
    public void on(MBFieldRef<BT, ?> fm) {
	// do nothing by default
    }
}
