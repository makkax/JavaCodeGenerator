package com.cc.jcg.hierarchy;

public class HierarchyImpl<NI>
	implements Hierarchy<NI> {

    private final ParentNode<NI> root;

    public HierarchyImpl(HierarchyBuilder<NI> builder, NI item) {
	super();
	this.root = new NodeImpl(builder, this, item);
    }

    @Override
    public final ParentNode<NI> getRootNode() {
	return root;
    }

    @Override
    public final void visit(HierarchyVisitor<NI> visitor) {
	_visit(root, visitor);
    }

    private void _visit(ParentNode<NI> parent, HierarchyVisitor<NI> visitor) {
	visitor.visit(parent);
	for (Node<NI> child : parent.getChildNodes()) {
	    _visit(child, visitor);
	}
    }
}
