package com.cc.jcg.bean.example;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import com.cc.jcg.bean.MBBean;

@MBBean
public final class ExampleBeanB
	implements Serializable {

    private static final long serialVersionUID = -3527080561990262983L;
    private String name;
    private Calendar calendar;
    private PrototypeBeanMeta back;
    private List<PrototypeBeanMeta> backs;

    public synchronized String getName() {
	return name;
    }

    public synchronized void setName(String name) {
	this.name = name;
    }

    public synchronized Calendar getCalendar() {
	return calendar;
    }

    public synchronized void setCalendar(Calendar calendar) {
	this.calendar = calendar;
    }

    public synchronized PrototypeBeanMeta getBack() {
	return back;
    }

    public synchronized void setBack(PrototypeBeanMeta back) {
	this.back = back;
    }

    public synchronized List<PrototypeBeanMeta> getBacks() {
	return backs;
    }

    public synchronized void setBacks(List<PrototypeBeanMeta> backs) {
	this.backs = backs;
    }
}
