package com.cc.jcg.hierarchy;

public interface Hierarchy<NI> {

    ParentNode<NI> getRootNode();

    void visit(HierarchyVisitor<NI> visitor);
}
