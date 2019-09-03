package com.cc.jcg;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

public interface MType {

    void addImport(Class<?> type);

    void addImport(MTypeRef ref);

    void addImport(String fullname);

    Object addExtraImport(Class<?> type);

    Object addExtraImport(MTypeRef ref);

    Object addExtraImport(String fullname);

    File getSrcFile();

    MPackage getPckg();

    String getPackageName();

    String getName();

    String getQualifiedName();

    TreeSet<String> getImports();

    Class<?> getJavaType() throws ClassNotFoundException;

    Set<? extends MMethod> getMethods();

    MType setTemplate(boolean boo);

    boolean isTemplate();
}
