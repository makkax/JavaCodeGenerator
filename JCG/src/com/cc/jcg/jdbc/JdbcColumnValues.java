package com.cc.jcg.jdbc;

import java.util.List;

public interface JdbcColumnValues<T>
	extends JdbcColumn<T> {

    List<T> getValues();

    void setValue(List<T> values);

    @Override
    default boolean isValueSet() {
	return getValues() != null && !getValues().isEmpty();
    }
}
