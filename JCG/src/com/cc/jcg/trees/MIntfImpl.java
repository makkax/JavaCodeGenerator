package com.cc.jcg.trees;

import com.cc.jcg.MClass;
import com.cc.jcg.MInterface;
import com.cc.jcg.MTypeRef;

public interface MIntfImpl {

    MInterface getInterface();

    MClass getImplementation();

    public static interface MIntfImplRef {

	MTypeRef getInterface();

	MTypeRef getImplementation();
    }
}
