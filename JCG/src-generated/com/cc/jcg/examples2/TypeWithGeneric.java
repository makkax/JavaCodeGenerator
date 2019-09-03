package com.cc.jcg.examples2;

import java.math.BigDecimal;

public interface TypeWithGeneric<GENERIC> {

    BigDecimal get();

    void set(GENERIC some);
}
