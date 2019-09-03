package com.cc.jcg.bean;

public interface MBFieldListener<BT> {

    void on(MBFieldDef<BT> fm);

    void on(MBField<BT, ?> fm);

    void on(MBFieldCollection<BT, ?> fm);

    void on(MBFieldRef<BT, ?> fm);
}
