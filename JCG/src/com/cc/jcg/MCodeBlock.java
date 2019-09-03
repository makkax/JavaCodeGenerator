package com.cc.jcg;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public final class MCodeBlock
	implements Serializable {

    private static final long serialVersionUID = 1L;
    private static String TAB = "    ";
    private final LinkedList<String> before;
    private final LinkedList<String> lines;
    private final LinkedList<String> after;
    private final AtomicInteger tabs = new AtomicInteger(0);
    private boolean disableIdentation = false;

    public static final synchronized void setTab(String tab) {
	TAB = tab;
    }

    public static final synchronized String tab() {
	return tabs(1);
    }

    public static final synchronized String tabs(int count) {
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < count; i++) {
	    sb.append(TAB);
	}
	return sb.toString();
    }

    public MCodeBlock() {
	this(new LinkedList<String>());
    }

    public MCodeBlock(boolean disableIdentation) {
	this();
	this.disableIdentation = disableIdentation;
    }

    public MCodeBlock(LinkedList<String> lines) {
	this(lines, null, null);
    }

    public MCodeBlock(String before, String after) {
	this(new LinkedList<String>(), before, after);
    }

    public MCodeBlock(StringBuffer before, String after) {
	this(new LinkedList<String>(), before.toString(), after);
    }

    public MCodeBlock(String before, StringBuffer after) {
	this(new LinkedList<String>(), before, after.toString());
    }

    public MCodeBlock(StringBuffer before, StringBuffer after) {
	this(new LinkedList<String>(), before.toString(), after.toString());
    }

    public MCodeBlock(LinkedList<String> lines, String before, String after) {
	super();
	this.before = new LinkedList<String>();
	if (before != null) {
	    this.before.add(before);
	}
	this.lines = lines;
	this.after = new LinkedList<String>();
	if (after != null) {
	    this.after.add(after);
	}
    }

    public MCodeBlock(String line) {
	this();
	addLine(line);
    }

    public MCodeBlock(StringBuffer line) {
	this();
	addLine(line);
    }

    public synchronized LinkedList<String> getInnerCodeLines() {
	return lines;
    }

    public synchronized LinkedList<String> getLines() {
	LinkedList<String> copy = new LinkedList<String>();
	for (String line : before) {
	    copy.add(tabs(line));
	}
	incrementTabs();
	for (String line : lines) {
	    copy.add(tabs(line));
	}
	decrementTabs();
	for (String line : after) {
	    copy.add(tabs(line));
	}
	return copy;
    }

    private String tabs(String line) {
	StringBuffer sb = new StringBuffer();
	String[] iLines = line.split("\\n");
	for (int l = 0; l < iLines.length; l++) {
	    for (int i = 0; i < tabs.get(); i++) {
		sb.append(TAB);
	    }
	    sb.append(iLines[l] + (iLines.length == 1 || l == iLines.length - 1 ? "" : "\n"));
	}
	return sb.toString();
    }

    public synchronized void addLine(String line) {
	if (line != null) {
	    lines.add(line);
	}
    }

    public synchronized void addLine(StringBuffer line) {
	if (line != null) {
	    lines.add(line.toString());
	}
    }

    public synchronized void addEmptyLine(boolean really) {
	if (really) {
	    lines.add("");
	}
    }

    public synchronized void addEmptyLine() {
	addEmptyLine(true);
    }

    public synchronized void addLines(Collection<String> lines) {
	for (String line : lines) {
	    addLine(line);
	}
    }

    public synchronized MCodeBlock incrementTabs() {
	if (!disableIdentation) {
	    tabs.incrementAndGet();
	}
	return this;
    }

    public synchronized MCodeBlock decrementTabs() {
	if (!disableIdentation) {
	    tabs.decrementAndGet();
	}
	return this;
    }
}
