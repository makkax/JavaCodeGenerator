package com.cc.jcg.jdbc.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class JDBCConnectionProviderMSSQL
	implements JDBCConnectionProvider {

    private static final String JDBC_DRIVER_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    static {
	try {
	    System.out.println("loading JDBC_DRIVER_CLASS = " + JDBC_DRIVER_CLASS);
	    Class.forName(JDBC_DRIVER_CLASS);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	}
    }
    private final String name;
    private final String url;
    private final String user;
    private final String password;

    public JDBCConnectionProviderMSSQL(String name, String url, String user, String password) {
	super();
	this.name = name;
	this.url = url;
	this.user = user;
	this.password = password;
    }

    public JDBCConnectionProviderMSSQL(String url, String user, String password) {
	super();
	this.name = "JDBCConnectionProviderMSSQL#" + hashCode();
	this.url = url;
	this.user = user;
	this.password = password;
    }

    public JDBCConnectionProviderMSSQL(Properties properties) {
	super();
	this.name = "JDBCConnectionProviderMSSQL#" + hashCode();
	this.url = properties.getProperty("jdbc.url", "jdbc.url");
	this.user = properties.getProperty("jdbc.username", "jdbc.username");
	this.password = properties.getProperty("jdbc.password", "jdbc.password");
    }

    @Override
    public String toString() {
	return this.getClass().getSimpleName() + " " + name + ": " + url;
    }

    public String getName() {
	return name;
    }

    @Override
    public Connection getNewConnection() throws SQLException {
	return DriverManager.getConnection(url, user, password);
    }
}
