package com.cc.jcg.main;

import com.cc.jcg.MClass.MClassModifier;
import com.cc.jcg.MEnumDispatcher;

public abstract class ClassModifierDispatcher<R>
        implements MEnumDispatcher<MClassModifier, R> {
    
    public final R dispatch(MClassModifier value) {
        switch (value) {
            case PUBLIC:
                return PUBLIC();
            case PRIVATE:
                return PRIVATE();
            case DEFAULT:
                return DEFAULT();
        }
        throw new RuntimeException("unexpected value " + value);
    }
    
    protected abstract R PUBLIC();
    
    protected abstract R PRIVATE();
    
    protected abstract R DEFAULT();
}
