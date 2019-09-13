package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public interface QueryByCriteria<B> {

    Collection<B> executeQuery(Connection connection) throws SQLException;

    long count(Connection connection) throws SQLException;
}
