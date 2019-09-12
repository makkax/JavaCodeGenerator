package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.util.Collection;

public interface QueryByCriteria<B> {

    Collection<B> executeQuery(Connection connection);
}