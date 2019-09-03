package com.cc.jcg.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class FileWriter
	implements Callable<File> {

    private final File file;
    private BufferedWriter out;

    public FileWriter(File file) {
	super();
	this.file = file;
    }

    public final File call() throws Exception {
	out = null;
	try {
	    out = new BufferedWriter(new java.io.FileWriter(file));
	    synchronized (out) {
		writing();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    if (out != null) {
		out.flush();
		out.close();
	    }
	}
	return file;
    }

    protected abstract void writing() throws Exception;

    protected final void writeLine(String line) throws IOException {
	write(line + "\n");
    }

    protected final void write(String content) throws IOException {
	synchronized (out) {
	    out.write(content);
	}
    }
}
