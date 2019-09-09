package com.cc.jcg.json.gen;

import com.cc.jcg.MGenerated;
import com.cc.jcg.json.JsonBean;

@MGenerated
public class JsonToken2
        implements JsonBean {
    
    private String type;
    private String auth;
    private String comment;
    
    public JsonToken2() {
        super();
    }
    
    public final synchronized String getType() {
        return type;
    }
    
    public final synchronized void setType(String type) {
        this.type = type;
    }
    
    public final synchronized String getAuth() {
        return auth;
    }
    
    public final synchronized void setAuth(String auth) {
        this.auth = auth;
    }
    
    public final synchronized String getComment() {
        return comment;
    }
    
    public final synchronized void setComment(String comment) {
        this.comment = comment;
    }
}
