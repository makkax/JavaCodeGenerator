package com.cc.jcg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import com.cc.jcg.MMethod.MMethodModifier;

final class MGuessOverrides {

    private final MType type;
    private final Set<? extends MMethod> methods;

    MGuessOverrides(MType type) {
	super();
	this.type = type;
	this.methods = type.getMethods();
    }

    void call() {
	Set<MTypeRef> supertypes = new HashSet<MTypeRef>();
	if (type instanceof MClass) {
	    addSupertypes((MClass) type, supertypes);
	} else if (type instanceof MInterface) {
	    addSupertypes((MInterface) type, supertypes);
	} else if (type instanceof MEnum) {
	    addSupertypes((MEnum) type, supertypes);
	}
	for (MMethod m : methods) {
	    if (!m.getModifier().equals(MMethodModifier.PRIVATE) && !m.isStatic()) {
		for (MTypeRef ref : supertypes) {
		    if (hasMethod(ref, m)) {
			m.overrides();
			break;
		    }
		}
	    }
	}
    }

    private boolean hasMethod(MTypeRef ref, MMethod m) {
	if (ref instanceof MTypeRefModel) {
	    return hasMethod(((MTypeRefModel) ref).getRef(), m);
	} else if (ref instanceof MTypeRefJava) {
	    return hasMethod(((MTypeRefJava) ref).getRef(), m);
	}
	return false;
    }

    private boolean hasMethod(MType type, MMethod m) {
	for (MMethod tm : type.getMethods()) {
	    if (tm.equals(m)) {
		return true;
	    }
	}
	return false;
    }

    private boolean hasMethod(Class<?> type, MMethod m) {
	for (Method tm : type.getDeclaredMethods()) {
	    if (!Modifier.isStatic(tm.getModifiers())) {
		if (tm.getName().equals(m.getName())) {
		    if (tm.getParameterTypes().length == m.getParameters().size()) {
			int i = -1;
			boolean e = true;
			for (MParameter p : m.getParameters()) {
			    i = i + 1;
			    MTypeRef pt = p.getType();
			    Object ref = pt.getRef();
			    String name = ref instanceof Class ? ((Class) ref).getName() : ((MType) ref).getQualifiedName();
			    if (!tm.getParameterTypes()[i].getName().equals(name)) {
				e = false;
				break;
			    }
			}
			if (e) {
			    return true;
			}
		    }
		}
	    }
	}
	return false;
    }

    private void addSupertypes(MClass type, Set<MTypeRef> supertypes) {
	if (type.getSuperclass() != null) {
	    supertypes.add(type.getSuperclass());
	    addSupertypes(type.getSuperclass(), supertypes);
	}
	for (MTypeRef intf : type.getInterfaces()) {
	    if (!supertypes.contains(intf)) {
		supertypes.add(intf);
		addSupertypes(intf, supertypes);
	    }
	}
    }

    private void addSupertypes(MInterface type, Set<MTypeRef> supertypes) {
	for (MTypeRef intf : type.getInterfaces()) {
	    if (!supertypes.contains(intf)) {
		supertypes.add(intf);
		addSupertypes(intf, supertypes);
	    }
	}
    }

    private void addSupertypes(MEnum type, Set<MTypeRef> supertypes) {
	for (MTypeRef intf : type.getInterfaces()) {
	    if (!supertypes.contains(intf)) {
		supertypes.add(intf);
		addSupertypes(intf, supertypes);
	    }
	}
    }

    private void addSupertypes(MTypeRef ref, Set<MTypeRef> supertypes) {
	if (ref instanceof MTypeRefModel) {
	    addSupertypes((MTypeRefModel) ref, supertypes);
	} else if (ref instanceof MTypeRefJava) {
	    addSupertypes(((MTypeRefJava) ref).getRef(), supertypes);
	}
    }

    private void addSupertypes(Class<?> type, Set<MTypeRef> supertypes) {
	if (type.getSuperclass() != null) {
	    supertypes.add(new MTypeRefJava(type.getSuperclass()));
	    addSupertypes(type.getSuperclass(), supertypes);
	}
	for (Class<?> intf : type.getInterfaces()) {
	    if (!supertypes.contains(intf)) {
		supertypes.add(new MTypeRefJava(intf));
		addSupertypes(intf, supertypes);
	    }
	}
    }

    private void addSupertypes(MTypeRefModel ref, Set<MTypeRef> supertypes) {
	MType type = ref.getRef();
	if (type instanceof MClass) {
	    addSupertypes((MClass) type, supertypes);
	} else if (type instanceof MInterface) {
	    addSupertypes((MInterface) type, supertypes);
	}
    }
}
