package com.cc.jcg;

public interface MGeneratedCodeListener {

    void onNew(MAnnotation type);

    void onNew(MInterface type);

    void on(MInterface type, String line);

    void on(MAnnotation type, String line);

    void onNew(MClass type);

    void on(MClass type, String line);

    void onNew(MEnum type);

    void on(MEnum type, String line);

    void onNew(MJavaFile type);

    void on(MJavaFile type, String line);
}
