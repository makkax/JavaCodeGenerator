package com.cc.jcg.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface JDBCConnectionProvider {

    Connection getNewConnection() throws SQLException;
}
