package com.cc.jcg.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public interface JdbcColumn<T>
	extends Comparable<JdbcColumn<?>>, Enabled {

    Class<T> getValueType();

    String getTableName();

    String getColumnName();

    Integer getSortOrder();

    void setSortOrder(Integer sortOrder);

    String getLabel();

    void setLabel(String label);

    boolean isValueSet();

    @Override
    default int compareTo(JdbcColumn o) {
	Integer so1 = getSortOrder();
	Integer so2 = o.getSortOrder();
	if (so1 == null && so2 == null) {
	    return getColumnName().compareTo(o.getColumnName());
	}
	if ((so1 == null && so2 != null) || (so1 != null && so2 == null) || so1.equals(so2)) {
	    return getColumnName().compareTo(o.getColumnName());
	}
	return so1.compareTo(so2);
    }

    void where(StringBuffer sql);

    void properties(PreparedStatement stm, AtomicInteger parameterIndex) throws SQLException;
}
