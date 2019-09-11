package com.cc.jcg.jdbc;

public interface JdbcColumnValue<T>
	extends JdbcColumn<T> {

    T getValue();

    void setValue(T value);

    @Override
    default boolean isValueSet() {
	return getValue() != null && !getValue().toString().trim().isEmpty();
    }
}
