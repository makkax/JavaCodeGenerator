package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface JDBCConnectionProvider {

    Connection getNewConnection() throws SQLException;
}
