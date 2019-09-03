package com.cc.jcg;

import java.util.TreeSet;

public interface MHasImports {

    TreeSet<String> getImports();

    void addImport(Class<?> type);

    void addImport(MTypeRef ref);

    void addImport(String fullname);

    void addImport(MType type);
}
