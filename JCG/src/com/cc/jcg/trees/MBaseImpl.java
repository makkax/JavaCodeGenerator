package com.cc.jcg.trees;

import com.cc.jcg.MClass;
import com.cc.jcg.MTypeRef;

public interface MBaseImpl {

    MClass getBaseClass();

    MClass getImplementation();

    public static interface MBaseImplRef {

	MTypeRef getBaseClass();

	MTypeRef getImplementation();
    }
}
