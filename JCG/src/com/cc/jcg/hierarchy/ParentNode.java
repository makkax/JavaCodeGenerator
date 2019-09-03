package com.cc.jcg.hierarchy;

import java.util.Set;

public interface ParentNode<NI> {

    NI getNodeItem();

    Set<Node<NI>> getChildNodes();
}
