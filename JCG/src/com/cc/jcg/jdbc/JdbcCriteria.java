package com.cc.jcg.jdbc;

import java.util.Collection;

public interface JdbcCriteria {

    public static final int UNLIMITED_RESULTS = 0;

    String getTableName();

    void setTableName(String tableName);

    int getMaxResults();

    void setMaxResults(int maxResults);

    Collection<JdbcColumn<?>> getColumns();
}
