package com.cc.jcg;

public interface MTypeRef<REF> {

    REF getRef();

    String getPckg();

    String getSimpleName();

    String getQualifiedName();

    String getImport();

    boolean isSerializable();

    boolean isVoid();
}
