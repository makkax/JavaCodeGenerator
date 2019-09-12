package com.cc.jcg.jdbc;

import java.sql.Connection;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.cc.jcg.jdbc.JdbcCriteria;
import com.cc.jcg.jdbc.JdbcCriteriaBase;
import com.cc.jcg.jdbc.QueryByCriteria;

public class SimpleCriteria
	extends JdbcCriteriaBase
	implements JdbcCriteria, QueryByCriteria<SimpleEntity> {

    private String firstName;
    private String lastName;
    private Date date;
    private boolean active;
    private List<String> items;

    public SimpleCriteria(String arg0, int arg1) {
	super(arg0, arg1);
    }

    public SimpleCriteria(String arg0) {
	super(arg0);
    }

    public SimpleCriteria() {
	super();
    }

    @Override
    protected void addColumns() {
	addJdbcColumnValue("FIRST_NAME", java.lang.String.class, this::getFirstName, this::setFirstName);
	addJdbcColumnValue("LAST_NAME", java.lang.String.class, this::getLastName, this::setLastName);
	addJdbcColumnValue("DATE", java.util.Date.class, this::getDate, this::setDate);
	addJdbcColumnValue("ACTIVE", boolean.class, this::isActive, this::setActive);
	addJdbcColumnValues("ITEMS", java.lang.String.class, this::getItems, this::setItems);
    }

    public final synchronized String getFirstName() {
	return firstName;
    }

    public final synchronized void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public final synchronized String getLastName() {
	return lastName;
    }

    public final synchronized void setLastName(String lastName) {
	this.lastName = lastName;
    }

    public final synchronized Date getDate() {
	return date;
    }

    public final synchronized void setDate(Date date) {
	this.date = date;
    }

    public final synchronized boolean isActive() {
	return active;
    }

    public final synchronized void setActive(boolean active) {
	this.active = active;
    }

    public final synchronized List<String> getItems() {
	return items;
    }

    public final synchronized void setItems(List<String> items) {
	this.items = items;
    }

    @Override
    public Collection<SimpleEntity> executeQuery(Connection connection) {
	return null;
    }
}