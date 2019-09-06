package com.cc.jcg.json.gen;

import com.cc.jcg.MGenerated;
import com.cc.jcg.json.JsonBean;

@MGenerated
public class JsonToken
        implements JsonBean {
    
    private String type;
    private String value;
    
    public JsonToken() {
        super();
    }
    
    public final synchronized String getType() {
        return type;
    }
    
    public final synchronized void setType(String type) {
        this.type = type;
    }
    
    public final synchronized String getValue() {
        return value;
    }
    
    public final synchronized void setValue(String value) {
        this.value = value;
    }
}
