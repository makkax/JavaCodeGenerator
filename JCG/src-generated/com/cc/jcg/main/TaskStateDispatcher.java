package com.cc.jcg.main;

public abstract class TaskStateDispatcher<R> {
    
    public final R dispatch(TaskState value) {
        switch (value) {
            case READY:
                return READY();
            case RUNNING:
                return RUNNING();
            case FAILED:
                return FAILED();
            case SUCCEEDED:
                return SUCCEEDED();
        }
        throw new RuntimeException("unexpected value " + value);
    }
    
    protected abstract R READY();
    
    protected abstract R RUNNING();
    
    protected abstract R FAILED();
    
    protected abstract R SUCCEEDED();
}
