package com.cc.jcg.tools.xml;

import java.util.concurrent.Callable;

public interface XmlVisitor
	extends Callable<Void> {

    @Override
    public Void call() throws XmlVisitorException;
}
