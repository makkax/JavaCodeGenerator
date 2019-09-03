package com.cc.jcg.hierarchy;

public interface HierarchyVisitor<NI> {

    void visit(ParentNode<NI> item);
}
