package com.cc.jcg.hierarchy;

public interface Node<NI>
	extends ParentNode<NI> {

    Node<NI> getParentNode();
}
