package com.cc.jcg.examples;

public class TestBean {
    
    private String string;
    private Integer number;
    
    public TestBean() {
        super();
    }
    
    public final synchronized String getString() {
        return string;
    }
    
    public final synchronized void setString(String string) {
        this.string = string;
    }
    
    public final synchronized Integer getNumber() {
        return number;
    }
    
    public final synchronized void setNumber(Integer number) {
        this.number = number;
    }
}
