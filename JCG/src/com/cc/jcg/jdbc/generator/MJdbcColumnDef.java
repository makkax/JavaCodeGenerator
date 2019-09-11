package com.cc.jcg.jdbc.generator;

import java.util.concurrent.atomic.AtomicBoolean;

public interface MJdbcColumnDef {

    Class<?> getValueType();

    Integer getSortOrder();

    String getColumnName();

    String getLabel();

    AtomicBoolean isMultipleValues();
}
