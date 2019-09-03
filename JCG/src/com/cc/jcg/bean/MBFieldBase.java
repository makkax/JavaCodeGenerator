package com.cc.jcg.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

class MBFieldBase<BT, VT>
	implements MBField<BT, VT> {

    private final Class<BT> beanType;
    private final Class<VT> type;
    private final String name;

    MBFieldBase(Class<BT> beanType, Class<VT> type, String name) {
	super();
	this.beanType = beanType;
	this.type = type;
	this.name = name;
    }

    static final class MBFieldCollectionBase<BT, VT>
	    extends MBFieldBase<BT, Collection<VT>>
	    implements MBFieldCollection<BT, VT> {

	MBFieldCollectionBase(Class<BT> beanType, Class<Collection<VT>> type, String name) {
	    super(beanType, type, name);
	}
    }

    static class MBFieldRefBase<BT, VT>
	    extends MBFieldBase<BT, VT>
	    implements MBFieldRef<BT, VT> {

	MBFieldRefBase(Class<BT> beanType, Class<VT> type, String name) {
	    super(beanType, type, name);
	}

	@Override
	public MBBeanMeta<VT> getRef() {
	    return MBRegistry.getBeanMetaByBeanType(getBeanType());
	}
    }

    static final class MBFieldRefCollectionBase<BT, VT>
	    extends MBFieldBase<BT, Collection<VT>>
	    implements MBFieldRefCollection<BT, VT> {

	MBFieldRefCollectionBase(Class<BT> beanType, Class<Collection<VT>> type, String name) {
	    super(beanType, type, name);
	}

	@Override
	public MBBeanMeta<VT> getRef() {
	    return MBRegistry.getBeanMetaByBeanType(getBeanType());
	}
    }

    @Override
    public final Class<BT> getBeanType() {
	return beanType;
    }

    @Override
    public final Class<VT> getType() {
	return type;
    }

    @Override
    public final String getName() {
	return name;
    }

    static final Method getGetter(Class<?> beanType, Field field) {
	for (Method method : beanType.getMethods()) {
	    if (method.getParameterTypes().length == 0) {
		String camelize = field.getName().substring(0, 1).toUpperCase().concat(field.getName().substring(1));
		if (Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType())) {
		    if (method.getName().equals("is" + camelize)) {
			return method;
		    }
		} else {
		    if (method.getName().equals("get" + camelize)) {
			return method;
		    }
		}
	    }
	}
	return null;
    }

    @Override
    public final VT getValue(BT bean) {
	try {
	    Method method = getGetter(beanType, beanType.getDeclaredField(name));
	    return invokeMethod(bean, method);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e.getMessage());
	}
    }

    private VT invokeMethod(BT bean, Method method) {
	try {
	    method.setAccessible(true);
	    return (VT) method.invoke(bean);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e.getMessage());
	}
    }

    static final Method getSetter(Class<?> beanType, Field field) {
	for (Method method : beanType.getMethods()) {
	    if (method.getParameterTypes().length == 1) {
		String camelize = field.getName().substring(0, 1).toUpperCase().concat(field.getName().substring(1));
		if (method.getName().equals("set" + camelize)) {
		    return method;
		}
	    }
	}
	return null;
    }

    @Override
    public final void setValue(BT bean, VT value) {
	try {
	    Method method = getSetter(beanType, beanType.getDeclaredField(name));
	    Class<?> parameterType = method.getParameterTypes()[0];
	    boolean isCollection = Collection.class.isAssignableFrom(parameterType);
	    if (isCollection) {
		boolean isSameType = value != null && parameterType.isAssignableFrom(value.getClass());
		if (isSameType) {
		    method.setAccessible(true);
		    method.invoke(bean, value);
		} else {
		    Field field = beanType.getDeclaredField(name);
		    if (getGetter(beanType, field) != null) {
			Collection ref = (Collection) getValue(bean);
			ref.addAll((Collection) value);
		    } else {
			field.setAccessible(true);
			Collection collection = (Collection) field.get(bean);
			collection.addAll((Collection) value);
		    }
		}
	    } else {
		method.setAccessible(true);
		method.invoke(bean, value);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e.getMessage());
	}
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("MBFieldBase [beanType=");
	builder.append(beanType);
	builder.append(", type=");
	builder.append(type);
	builder.append(", name=");
	builder.append(name);
	builder.append("]");
	return builder.toString();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (beanType == null ? 0 : beanType.hashCode());
	result = prime * result + (name == null ? 0 : name.hashCode());
	result = prime * result + (type == null ? 0 : type.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	MBFieldBase other = (MBFieldBase) obj;
	if (beanType == null) {
	    if (other.beanType != null) {
		return false;
	    }
	} else if (!beanType.equals(other.beanType)) {
	    return false;
	}
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	if (type == null) {
	    if (other.type != null) {
		return false;
	    }
	} else if (!type.equals(other.type)) {
	    return false;
	}
	return true;
    }
}
