package com.cc.jcg.reflection;

import com.cc.jcg.test.ReflectionTest.SomeClass;
import java.time.LocalDate;

public class SomeClassExtension
        extends SomeClass {
    
    public SomeClassExtension(String arg0, LocalDate arg1, SomeClass arg2) {
        super(arg0, arg1, arg2);
    }
    
    public SomeClassExtension(String arg0, LocalDate arg1) {
        super(arg0, arg1);
    }
}
