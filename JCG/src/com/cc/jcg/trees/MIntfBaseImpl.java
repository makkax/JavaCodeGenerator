package com.cc.jcg.trees;

import com.cc.jcg.MClass;
import com.cc.jcg.MInterface;
import com.cc.jcg.MTypeRef;

public interface MIntfBaseImpl {

    MInterface getInterface();

    MClass getBaseClass();

    MClass getImplementation();

    public static interface MIntfBaseImplRef {

	MTypeRef getInterface();

	MTypeRef getBaseClass();

	MTypeRef getImplementation();
    }
}
