package com.cc.jcg.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public interface JdbcColumnValues<T>
	extends JdbcColumn<T> {

    List<T> getValues();

    void setValue(List<T> values);

    @Override
    default boolean isValueSet() {
	return getValues() != null && !getValues().isEmpty();
    }

    @Override
    default void where(StringBuffer sql) {
	sql.append(getColumnName() + " IN (" + getValues().stream().map(c -> "?").collect(Collectors.joining(",")) + ")");
    }

    @Override
    default void properties(PreparedStatement stm, AtomicInteger parameterIndex) throws SQLException {
	for (T v : getValues()) {
	    stm.setObject(parameterIndex.getAndIncrement(), v);
	}
    }
}
