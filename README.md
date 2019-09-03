# JavaCodeGenerator

JCG is a powerful Java code generator. 
Unlike most generators, JCG is not based on templates; its a DSL in pure Java!

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
Other highights:

* **imports** are automatically handled or can be manually added with the method `addImport`
* the generated code is automatically **indented**
* generated types can be **referenced** in other Java elements (see `MTypeRef<REF>`)
* **Generics** can be added to classes, interfaces and methods
* **Annotations** can be added to classes, interfaces, fields and methods

Each Java generated element is represented by a Java class that can be directly accessed to create the needed Java code:

| type            | related type                                                                       
|:--------------- |:-------------------------------------------------------------------------
| `MBundle`       |  `MPackage`
| `MPackage`      |  `MType`: `MClass`, `MInterface`, `MEnum`
| `MInterface`    |  `MMethod`
| `MClass`        |  `MField`, `MMethod`, `MConstructor`, `MInnerInterface`, `MInnerClass`, `MInnerEnum`
| `MEnum`         |  `MEnumValue`, `MField`, `MMethod`, `MConstructor` 
| `MAnnotation`   |  `MType`, `MField`, `MMethod`
| `MTypeRef<REF>` |  `MTypeRefGeneric` = `MTypeRef<String>`
|                 |  `MTypeRefJava`    = `MTypeRef<Class<?>>`
|                 |  `MTypeRefModel`   = `MTypeRef<MType>`

[show JCG model](https://github.com/alecbigger/JavaCodeGenerator/blob/master/JCG/CodeGeneratorDSL.png)

## More Examples

Here is another example where 10 classes implementing an interface "Named" are generated and each one contains a field referencing himself or another of the other 9 classes:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
MInterface intf = pckg.newInterface("NamedBean");
intf.addMethod("getName", String.class);
// ----------------------------------------------------------------------------------
List<MClass> classes = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    MClass cls = pckg.newClass("Bean0" + i);
    cls.addInterface(intf);
    classes.add(cls);
}
for (MClass cls : classes) {
    MField name = cls.addField(String.class, "name").setFinal(true);
    name.addGetterMethod().setFinal(true).overrides();
    cls.addFinalFieldsConstructor();
    int rand = Double.valueOf(10 * Math.random()).intValue();
    MField field = cls.addField(classes.get(rand), "bean0" + rand);
    field.addAccessorMethods();
}
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```

One of the classes could look something like this:

```java
package com.cc.jcg.main;

public class Bean05
        implements NamedBean {
    
    private final String name;
    private Bean00 bean00;
    
    public Bean05(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    public final synchronized Bean00 getBean00() {
        return bean00;
    }
    
    public final synchronized void setBean00(Bean00 bean00) {
        this.bean00 = bean00;
    }
}
```

To generate a `FunctionalInterface` we can write:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
MInterface intf = pckg.newInterface("Supplier").setGeneric("<T>");
intf.addMethod("get", "T");
// ----------------------------------------------------------------------------------
intf.addAnnotation(new FunctionalInterface() {

    @Override
    public Class<? extends Annotation> annotationType() {
    return FunctionalInterface.class;
    }
});
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```

to obtain

```java
package com.cc.jcg.main;

@FunctionalInterface
public interface Supplier<T> {
    
    T get();
}
```
