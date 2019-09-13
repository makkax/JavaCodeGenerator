package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class QueryExecutor<T> {

    private final JdbcCriteria criteria;

    public QueryExecutor(JdbcCriteria criteria) {
	super();
	this.criteria = criteria;
    }

    public Collection<T> executeQuery(Connection connection) throws SQLException {
	Collection<T> results = new ArrayList<>();
	ResultSet rs = execute(connection);
	Map<String, Integer> columns = new HashMap<>();
	criteria.getAllColumns().forEach(c -> columns.put(c.getColumnName(), columns.size() + 1));
	while (rs.next()) {
	    T e = newEntity();
	    fillEntity(e, rs, columns);
	    results.add(e);
	}
	return results;
    }

    protected abstract T newEntity();

    protected abstract void fillEntity(T e, ResultSet rs, Map<String, Integer> columns) throws SQLException;

    protected ResultSet execute(Connection connection) throws SQLException {
	StringBuffer sql = new StringBuffer();
	sql.append("SELECT ");
	sql.append(criteria.getAllColumns().stream().map(c -> c.getColumnName()).collect(Collectors.joining(", ")));
	sql.append(" FROM " + criteria.getTableName());
	sql.append(" WHERE 1=1");
	criteria.getEnabledColumns().stream().filter(JdbcColumn::isValueSet).forEach(c -> {
	    sql.append(" AND ");
	    c.where(sql);
	});
	if (criteria.getOrderByColumns().count() > 0) {
	    sql.append(" ORDER BY ");
	    String orderBy = criteria.getOrderByColumns().map(oc -> oc.getColumnName() + " " + (oc.getOrderBy().get() < 0 ? "DESC" : "ASC")).collect(Collectors.joining(", "));
	    sql.append(orderBy);
	}
	System.out.println(sql.toString());
	PreparedStatement stm = connection.prepareStatement(sql.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	stm.setFetchDirection(ResultSet.FETCH_FORWARD);
	AtomicInteger parameterIndex = new AtomicInteger(1);
	Optional<SQLException> failure = Optional.empty();
	criteria.getEnabledColumns().stream().filter(JdbcColumn::isValueSet).forEach(c -> {
	    try {
		c.properties(stm, parameterIndex);
	    } catch (SQLException e) {
		failure.of(e);
	    }
	});
	if (failure.isPresent()) {
	    throw failure.get();
	}
	if (criteria.getMaxResults() > JdbcCriteria.UNLIMITED_RESULTS) {
	    stm.setMaxRows(criteria.getMaxResults());
	}
	return stm.executeQuery();
    }
}