package com.cc.jcg.jdbc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConnectionProperties
	extends Properties {

    public ConnectionProperties(String path) throws FileNotFoundException, IOException {
	super();
	load(new FileInputStream(path));
    }

    public String getJdbcUrl() {
	return getProperty("jdbc.url");
    }

    public String getJdbcUsername() {
	return getProperty("jdbc.username");
    }

    public String getJdbcPassword() {
	return getProperty("jdbc.password");
    }
}
