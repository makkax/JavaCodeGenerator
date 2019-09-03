# JavaCodeGenerator

JCG is a powerful Java code generator. Differently from most generators, JCG is not based on templates; its a DSL in pure Java!

A simple example can show what that means:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
Collection<MParameter> fields = new ArrayList<>();
fields.add(new MParameter(String.class, "name"));
fields.add(new MParameter(String.class, "email"));
fields.add(new MParameter(LocalDate.class, "birthday"));
fields.add(new MParameter(boolean.class, "active"));
MClass bean = pckg.newBean("User", fields);
// ----------------------------------------------------------------------------------
MMethod activate = bean.addMethod("activate", void.class);
activate.setFinal(true);
activate.setSynchronized(true);
StringBuffer code = new StringBuffer();
code.append("active = true;\n");
code.append("// TODO: sendActivationEmail(email);");
activate.setBlockContent(code);
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```

The generated class User looks like this:

```java
package com.cc.jcg.main;

import java.time.LocalDate;

public class User {
    
    private String name;
    private String email;
    private LocalDate birthday;
    private boolean active;
    
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
    
    public final synchronized void activate() {
        active = true;
        // TODO: sendActivationEmail(email);
    }
}
```

Each Java generated package, type (class, interface, enum), constructor, field, method and parameter is represented by a Java class that can be directly accessed to create the needed Java code. The imports are automatically handled, generics can be used and references to generated types can be shared in the same MBundle.

Here is another example where 10 classes are generated and each one contains a field referencing himself or another of the other 9 classes:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
List<MClass> classes = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    MClass cls = pckg.newClass("Bean0" + i);
    classes.add(cls);
}
for (MClass cls : classes) {
    MField name = cls.addField(String.class, "name").setFinal(true);
    cls.addFinalFieldsConstructor();
    int rand = Double.valueOf(10 * Math.random()).intValue();
    MField field = cls.addField(classes.get(rand), "getBean0" + rand);
    field.addAccessorMethods();
}
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```
