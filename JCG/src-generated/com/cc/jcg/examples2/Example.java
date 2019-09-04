package com.cc.jcg.examples2;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class Example<T extends Serializable> {
    
    private final String name;
    private List<T> list;
    
    protected Example(String name) {
        super();
        this.name = name;
    }
    
    public final String getName() {
        return name;
    }
    
    public void getSimpleMethod() {
        System.getProperty("hey!");
    }
    
    public void getSimpleMethod2(String parameter) {
        System.getProperty("hey!");
    }
    
    public void getSimpleMethod3(MyEnum enumParameter) {
        // first line
        
        System.getProperty("hey!");
    }
    
    public SubclassExample getSimpleMethod4(MyEnum enumParameter) {
        return null;
    }
    
    public EqualsAndHash getJavaFile(EqualsAndHash file) {
        return null;
    }
    
    public List<T> getList() {
        return null;
    }
}
