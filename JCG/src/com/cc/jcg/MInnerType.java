package com.cc.jcg;

import java.util.Collection;

public interface MInnerType<T extends MCode<T>>
	extends MCode<T> {

    Collection<String> getImports();
}
