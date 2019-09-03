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

Each Java generated package, type (class, interface, enum), field, method and parameter is represented by a Java class that can be directly accessed to create the needed Java code.
