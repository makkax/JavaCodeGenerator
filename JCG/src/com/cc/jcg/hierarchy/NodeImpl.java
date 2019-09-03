package com.cc.jcg.hierarchy;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class NodeImpl<NI>
	implements Node<NI> {

    private final NI item;
    private final Node<NI> parent;
    private final Set<Node<NI>> children;

    NodeImpl(HierarchyBuilder<NI> builder, HierarchyImpl<NI> hierarchy, NI item) {
	this(builder, hierarchy, item, null, new HashMap<NI, NodeImpl<NI>>());
    }

    private NodeImpl(HierarchyBuilder<NI> builder, HierarchyImpl<NI> hierarchy, NI item, Node<NI> parent, Map<NI, NodeImpl<NI>> cache) {
	super();
	this.item = item;
	this.parent = parent;
	children = new LinkedHashSet<Node<NI>>();
	if (!cache.containsKey(item)) {
	    cache.put(item, this);
	    Collection<NI> children = builder.getChildren(item);
	    if (children != null) {
		for (NI child : children) {
		    if (cache.containsKey(child)) {
			this.children.add(cache.get(child));
		    } else {
			this.children.add(new NodeImpl<NI>(builder, hierarchy, child, this, cache));
		    }
		}
	    }
	}
    }

    @Override
    public NI getNodeItem() {
	return item;
    }

    @Override
    public Node<NI> getParentNode() {
	return parent;
    }

    @Override
    public Set<Node<NI>> getChildNodes() {
	return Collections.unmodifiableSet(children);
    }
}
