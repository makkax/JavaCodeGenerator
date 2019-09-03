package com.cc.jcg.test.types;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class HashAndEquals {

    private String name;
    private int number;
    private RefType type;

    public int hashCode1() {
	return HashCodeBuilder.reflectionHashCode(this);
    }

    public int hashCode2() {
	// you pick a hard-coded, randomly chosen, non-zero, odd number ideally different for each class
	HashCodeBuilder builder = new HashCodeBuilder(17, 37);
	builder.append(name);
	builder.append(number);
	return builder.toHashCode();
    }

    public boolean equals1(Object obj) {
	return EqualsBuilder.reflectionEquals(this, obj);// all fields,slow
    }

    public boolean equals2(Object obj) {
	if (!(obj instanceof HashAndEquals)) {
	    return false;
	}
	if (this == obj) {
	    return true;
	}
	HashAndEquals cst = (HashAndEquals) obj;// casted
	EqualsBuilder builder = new EqualsBuilder();
	builder.appendSuper(super.equals(obj));// super?
	builder.append(name, cst.name);// select fields!
	builder.append(number, cst.number);
	return builder.isEquals();
    }
}
