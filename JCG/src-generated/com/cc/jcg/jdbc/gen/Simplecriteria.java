package com.cc.jcg.jdbc.gen;

import com.cc.jcg.jdbc.JdbcCriteria;
import com.cc.jcg.jdbc.JdbcCriteriaBase;
import java.util.Date;
import java.util.List;

public class Simplecriteria
        extends JdbcCriteriaBase
        implements JdbcCriteria {
    
    private String firstName;
    private String lastName;
    private Date date;
    private Boolean active;
    private List<String> items;
    
    public Simplecriteria(String tableName, int maxResults) {
        super(tableName, maxResults);
    }
    
    public Simplecriteria(String tableName) {
        super(tableName);
    }
    
    public Simplecriteria() {
        super();
    }
    
    @Override
    protected void addColumns() {
        addJdbcColumnValue("FIRST_NAME", java.lang.String.class, this::getFirstName, this::setFirstName);
        addJdbcColumnValue("LAST_NAME", java.lang.String.class, this::getLastName, this::setLastName);
        addJdbcColumnValue("DATE", java.util.Date.class, this::getDate, this::setDate);
        addJdbcColumnValue("ACTIVE", java.lang.Boolean.class, this::isActive, this::setActive);
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
    
    public final synchronized Boolean isActive() {
        return active;
    }
    
    public final synchronized void setActive(Boolean active) {
        this.active = active;
    }
    
    public final synchronized List<String> getItems() {
        return items;
    }
    
    public final synchronized void setItems(List<String> items) {
        this.items = items;
    }
}
