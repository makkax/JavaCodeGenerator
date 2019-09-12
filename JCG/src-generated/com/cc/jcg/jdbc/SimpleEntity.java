package com.cc.jcg.jdbc;

import java.util.Date;

public class SimpleEntity {
    
    private String firstName;
    private String lastName;
    private Date date;
    private boolean active;
    private String items;
    
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
    
    public final synchronized String getItems() {
        return items;
    }
    
    public final synchronized void setItems(String items) {
        this.items = items;
    }
}
