package com.cc.jcg.jdbc;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface JdbcCriteria {

    public static final int UNLIMITED_RESULTS = 0;

    String getTableName();

    void setTableName(String tableName);

    int getMaxResults();

    void setMaxResults(int maxResults);

    Collection<JdbcColumn<?>> getAllColumns();

    default Collection<JdbcColumn<?>> getEnabledColumns() {
	return getAllColumns().stream().filter(JdbcColumn::isEnabled).collect(Collectors.toList());
    }

    default Collection<JdbcColumn<?>> getDisabledColumns() {
	return getAllColumns().stream().filter(c -> !c.isEnabled()).collect(Collectors.toList());
    }

    default Collection<JdbcColumn<?>> enableColumns() {
	return enableColumns(c -> true);
    }

    default void disableColumns() {
	enableColumns(c -> false);
    }

    default Collection<JdbcColumn<?>> enableColumns(Predicate<JdbcColumn<?>> test) {
	return getAllColumns().stream().map(c -> {
	    c.setEnabled(test.test(c));
	    return c;
	}).filter(JdbcColumn::isEnabled).collect(Collectors.toList());
    }
}
