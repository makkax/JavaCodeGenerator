# JavaCodeGenerator

JCG is a powerful Java code generator. 
Unlike most generators, JCG is not based on templates; its a **DSL in pure Java**!

A simple example can show what that means:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
Collection<MParameter> parameters = new ArrayList<>();
parameters.add(new MParameter(String.class, "name"));
parameters.add(new MParameter(String.class, "email"));
parameters.add(new MParameter(LocalDate.class, "birthday"));
parameters.add(new MParameter(boolean.class, "active"));
// List<String> tags
parameters.add(new MParameter(List.class, String.class, "tags"));
// Map<String, User> tags
parameters.add(new MParameter(Map.class, "<String, User>", "friends"));
MClass bean = pckg.newBean("User", parameters);
// ----------------------------------------------------------------------------------
MMethod activate = bean.addMethod("activate", void.class);
activate.setFinal(true);
activate.setSynchronized(true);
StringBuffer code = new StringBuffer();
code.append("active = true;\n");
code.append("// TODO: sendActivationEmail(email);");
activate.setBlockContent(code);
// ----------------------------------------------------------------------------------
MMethod deactivate = bean.addMethod("deactivate", void.class);
deactivate.setFinal(true);
deactivate.setSynchronized(true);
deactivate.setCodeBlock(block -> {
    block.addLine("active = false;");
    block.addLine("// TODO: sendDeactivationEmail(email);");
});
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```

The generated class `User` will look like this:

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
```
Other highights:

* **imports** are automatically handled or can be manually added with the method `addImport`
* the generated code is automatically **indented**
* generated types can be **referenced** in other Java elements (see `MTypeRef<REF>`)
* **Generics** can be added to classes, interfaces and methods
* **Annotations** can be added to classes, interfaces, fields and methods
* a basic **Exceptions handling** is configurable for methods

Each Java generated element is represented by a class that can be directly accessed to create the needed code:

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

All examples listed here are included in the `src-eclipse` project directory (class `MainExamples`).

Here is another example where 10 classes implementing an interface `Named` (also generated) are created and each one contains a field referencing another `Named` type:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
bundle.setShowProgressbar(true);
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
MInterface intf = pckg.newInterface("NamedBean");
intf.addMethod("getName", String.class);
intf.addMethod("getBean", intf);
// ----------------------------------------------------------------------------------
List<MClass> classes = new ArrayList<>();
for (int i = 0; i < 10; i++) {
    MClass cls = pckg.newClass("Bean0" + i);
    cls.addInterface(intf);
    classes.add(cls);
}
int i = 9;
for (MClass cls : classes) {
    MField name = cls.addField(String.class, "name").setFinal(true);
    name.addGetterMethod().setFinal(true).overrides();
    cls.addFinalFieldsConstructor();
    MField field = cls.addField(classes.get(i), "bean");
    field.addAccessorMethods().getter().overrides();
    i = i - 1;
}
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
// ----------------------------------------------------------------------------------
```

The `Bean05` generated class will look like this:

```java
package com.cc.jcg.main;

public class Bean05
        implements NamedBean {
    
    private final String name;
    private Bean04 bean;
    
    public Bean05(String name) {
        super();
        this.name = name;
    }
    
    @Override
    public final String getName() {
        return name;
    }
    
    @Override
    public final synchronized Bean04 getBean() {
        return bean;
    }
    
    public final synchronized void setBean(Bean04 bean) {
        this.bean = bean;
    }
}
```

The following code will generate a `FunctionalInterface`, using also a Generic type `T`:

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

The generated interface will be annotatd with `@MGenerated`; this annotation can be omitted by using the static flag

`MBundle.EXCLUDE_GENERATED_ANNOTATION.set(true);` (default value is `true`)

```java
package com.cc.jcg.main;

import com.cc.jcg.MGenerated;

@MGenerated
@FunctionalInterface
public interface Supplier<T> {
    
    T get();
}
```

The following example generates a new Enumeration `TaskState` with a corresponding `TaskStateDispatcher` class and another dispatcher for an already existing Enumeration `MClassModifier`:

```java
// ----------------------------------------------------------------------------------
MBundle bundle = new MBundle(new File("src-generated"));
MPackage pckg = bundle.newPackage("com.cc.jcg.main");
// ----------------------------------------------------------------------------------
MEnum enm = pckg.newEnum("TaskState");
enm.addValue("READY");
enm.addValue("RUNNING");
enm.addValue("FAILED");
enm.addValue("SUCCEEDED");
MClass dis1 = enm.newDispatcher();
// ----------------------------------------------------------------------------------
MClass dis2 = pckg.newEnumDispatcher(MClassModifier.class, "ClassModifierDispatcher");
dis2.addInterface(MEnumDispatcher.class, "<MClassModifier, R>");
// ----------------------------------------------------------------------------------
boolean clean = false;
bundle.generateCode(clean);
```

TaskState.java:
```java
package com.cc.jcg.main;

public enum TaskState {
    READY,
    RUNNING,
    FAILED,
    SUCCEEDED;
    
}
```

TaskStateDispatcher.java:
```java
package com.cc.jcg.main;

public abstract class TaskStateDispatcher<R> {
    
    public final R dispatch(TaskState value) {
        switch (value) {
            case READY:
                return READY();
            case RUNNING:
                return RUNNING();
            case FAILED:
                return FAILED();
            case SUCCEEDED:
                return SUCCEEDED();
        }
        throw new RuntimeException("unexpected value " + value);
    }
    
    protected abstract R READY();
    
    protected abstract R RUNNING();
    
    protected abstract R FAILED();
    
    protected abstract R SUCCEEDED();
}
```

ClassModifierDispatcher.java:
```java
package com.cc.jcg.main;

import com.cc.jcg.MClass.MClassModifier;
import com.cc.jcg.MEnumDispatcher;

public abstract class ClassModifierDispatcher<R>
        implements MEnumDispatcher<MClassModifier, R> {
    
    public final R dispatch(MClassModifier value) {
        switch (value) {
            case PUBLIC:
                return PUBLIC();
            case PRIVATE:
                return PRIVATE();
            case DEFAULT:
                return DEFAULT();
        }
        throw new RuntimeException("unexpected value " + value);
    }
    
    protected abstract R PUBLIC();
    
    protected abstract R PRIVATE();
    
    protected abstract R DEFAULT();
}
```
