package com.cc.jcg.main;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class User {
    
    private String name;
    private String email;
    private LocalDate birthday;
    private boolean active;
    private List<String> tags;
    private Map<String, User> friends;
    
    public User() {
        super();
    }
    
    public final synchronized String getName() {
        return name;
    }
    
    public final synchronized void setName(String name) {
        this.name = name;
    }
    
    public final synchronized String getEmail() {
        return email;
    }
    
    public final synchronized void setEmail(String email) {
        this.email = email;
    }
    
    public final synchronized LocalDate getBirthday() {
        return birthday;
    }
    
    public final synchronized void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public final synchronized boolean isActive() {
        return active;
    }
    
    public final synchronized void setActive(boolean active) {
        this.active = active;
    }
    
    public final synchronized List<String> getTags() {
        return tags;
    }
    
    public final synchronized void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public final synchronized Map<String, User> getFriends() {
        return friends;
    }
    
    public final synchronized void setFriends(Map<String, User> friends) {
        this.friends = friends;
    }
    
    public final synchronized void activate() {
        active = true;
        // TODO: sendActivationEmail(email);
    }
    
    public final synchronized void deactivate() {
        active = false;
        // TODO: sendDeactivationEmail(email);
    }
}
