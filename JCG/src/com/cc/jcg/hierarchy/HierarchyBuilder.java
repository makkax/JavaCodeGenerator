package com.cc.jcg.hierarchy;

import java.util.Collection;

public interface HierarchyBuilder<NI> {

    Collection<NI> getChildren(NI item);
}
