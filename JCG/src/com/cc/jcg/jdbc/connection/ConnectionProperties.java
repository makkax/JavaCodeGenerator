package com.cc.jcg.jdbc.connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.cc.jcg.tools.SortedProperties;

public class ConnectionProperties
	extends SortedProperties {

    private final String path;

    public ConnectionProperties(File file) throws FileNotFoundException, IOException {
	this(file.getPath());
    }

    public ConnectionProperties(String path) throws FileNotFoundException, IOException {
	this(path, false);
    }

    public ConnectionProperties(File file, boolean sorted) throws FileNotFoundException, IOException {
	this(file.getPath(), sorted);
    }

    public ConnectionProperties(String path, boolean sorted) throws FileNotFoundException, IOException {
	super(sorted);
	this.path = path;
	load(new FileInputStream(path));
    }

    public void save() throws FileNotFoundException, IOException {
	store(new FileOutputStream(path), null);
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
