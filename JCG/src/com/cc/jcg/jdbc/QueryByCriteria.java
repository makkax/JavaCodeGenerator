package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.function.Consumer;

public interface QueryByCriteria<B> {

    Collection<B> executeQuery(Connection connection) throws SQLException;

    void executeQuery(Connection connection, Consumer<B> consumer) throws SQLException;

    long count(Connection connection) throws SQLException;
}
