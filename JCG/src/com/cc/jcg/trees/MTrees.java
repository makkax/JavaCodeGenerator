package com.cc.jcg.trees;

import com.cc.jcg.MClass;
import com.cc.jcg.MInterface;
import com.cc.jcg.MTypeRef;
import com.cc.jcg.trees.MBaseImpl.MBaseImplRef;
import com.cc.jcg.trees.MIntfBaseImpl.MIntfBaseImplRef;
import com.cc.jcg.trees.MIntfImpl.MIntfImplRef;

public final class MTrees {

    public static MBaseImpl getBaseImpl(final MClass BaseClass, final MClass Implementation) {
	Implementation.setSuperclass(BaseClass);
	return new MBaseImpl() {

	    @Override
	    public MClass getBaseClass() {
		return BaseClass;
	    }

	    @Override
	    public MClass getImplementation() {
		return Implementation;
	    }
	};
    }

    public static MBaseImplRef getBaseImplRef(final MTypeRef BaseClass, final MTypeRef Implementation) {
	return new MBaseImplRef() {

	    @Override
	    public MTypeRef getBaseClass() {
		return BaseClass;
	    }

	    @Override
	    public MTypeRef getImplementation() {
		return Implementation;
	    }
	};
    }

    public static MIntfImpl getIntfImpl(final MInterface Interface, final MClass Implementation) {
	Implementation.addInterface(Interface);
	return new MIntfImpl() {

	    @Override
	    public MInterface getInterface() {
		return Interface;
	    }

	    @Override
	    public MClass getImplementation() {
		return Implementation;
	    }
	};
    }

    public static MIntfImplRef getIntfImplRef(final MTypeRef Interface, final MTypeRef Implementation) {
	return new MIntfImplRef() {

	    @Override
	    public MTypeRef getInterface() {
		return Interface;
	    }

	    @Override
	    public MTypeRef getImplementation() {
		return Implementation;
	    }
	};
    }

    public static MIntfBaseImpl getIntfBaseImpl(final MInterface Interface, final MClass BaseClass, final MClass Implementation) {
	BaseClass.addInterface(Interface);
	Implementation.setSuperclass(BaseClass);
	return new MIntfBaseImpl() {

	    @Override
	    public MInterface getInterface() {
		return Interface;
	    }

	    @Override
	    public MClass getBaseClass() {
		return BaseClass;
	    }

	    @Override
	    public MClass getImplementation() {
		return Implementation;
	    }
	};
    }

    public static MIntfBaseImplRef getIntfBaseImplRef(final MTypeRef Interface, final MTypeRef BaseClass, final MTypeRef Implementation) {
	return new MIntfBaseImplRef() {

	    @Override
	    public MTypeRef getInterface() {
		return Interface;
	    }

	    @Override
	    public MTypeRef getBaseClass() {
		return BaseClass;
	    }

	    @Override
	    public MTypeRef getImplementation() {
		return Implementation;
	    }
	};
    }

    private MTrees() {
	super();
    }
}
