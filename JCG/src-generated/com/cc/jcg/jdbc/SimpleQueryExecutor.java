package com.cc.jcg.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class SimpleQueryExecutor
        extends QueryExecutor<SimpleEntity> {
    
    public SimpleQueryExecutor(JdbcCriteria criteria) {
        super(criteria);
    }
    
    @Override
    public SimpleEntity newEntity() {
        return new SimpleEntity();
    }
    
    @Override
    public void fillEntity(SimpleEntity e, ResultSet rs, Map<String, Integer> columns) throws SQLException {
        e.setFirstName(rs.getString(columns.get("FIRST_NAME")));
        e.setLastName(rs.getString(columns.get("LAST_NAME")));
        e.setDate(rs.getDate(columns.get("DATE")));
        e.setActive(rs.getBoolean(columns.get("ACTIVE")));
        e.setItems(rs.getString(columns.get("ITEMS")));
    }
}
