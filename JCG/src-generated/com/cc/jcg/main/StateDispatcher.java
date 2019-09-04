package com.cc.jcg.main;

public abstract class StateDispatcher<R> {
    
    public final R dispatch(State value) {
        switch (value) {
            case READY:
                return READY();
            case RUNNING:
                return RUNNING();
            case FAILED:
                return FAILED();
            case SECCEED:
                return SECCEED();
        }
        throw new RuntimeException("unexpected value " + value);
    }
    
    protected abstract R READY();
    
    protected abstract R RUNNING();
    
    protected abstract R FAILED();
    
    protected abstract R SECCEED();
}
