package com.cc.jcg.reflection;

import com.cc.jcg.test.ReflectionTest.SomeClass;
import java.time.LocalDate;

public class SomeClassExtension
        extends SomeClass {
    
    public SomeClassExtension(String name, LocalDate date, SomeClass other) {
        super(name, date, other);
    }
    
    public SomeClassExtension(String name, LocalDate date) {
        super(name, date);
    }
}
