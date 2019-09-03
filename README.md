# JavaCodeGenerator

JCG is a powerful Java code generator. Differently from most generators, JCG is not based on templates; its a DSL in pure Java!

A simple example can show what that means:

```java
MBundle bundle = ...
```

Each Java generated package, type (class, interface, enum), field, method and parameter is represented by a Java class that can be directly accessed to create the needed Java code.
