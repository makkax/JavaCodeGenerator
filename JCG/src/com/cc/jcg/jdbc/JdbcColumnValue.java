package com.cc.jcg.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public interface JdbcColumnValue<T>
	extends JdbcColumn<T> {

    T getValue();

    void setValue(T value);

    @Override
    default boolean isValueSet() {
	return getValue() != null && !getValue().toString().trim().isEmpty();
    }

    @Override
    default void where(StringBuffer sql) {
	sql.append(getColumnName() + " = ?");
    }

    @Override
    default void properties(PreparedStatement stm, AtomicInteger parameterIndex) throws SQLException {
	stm.setObject(parameterIndex.getAndIncrement(), getValue());
    }
}
