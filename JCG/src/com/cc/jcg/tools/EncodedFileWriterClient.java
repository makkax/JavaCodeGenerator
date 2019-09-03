package com.cc.jcg.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public final class EncodedFileWriterClient {

    private final File file;
    private final String encoding;
    private BufferedWriter out;

    public EncodedFileWriterClient(File file) {
	this(file, "UTF8");
    }

    public EncodedFileWriterClient(File file, String encoding) {
	super();
	this.file = file;
	this.encoding = encoding;
    }

    public void open() throws Exception {
	synchronized (file) {
	    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
	}
    }

    public void flush() throws Exception {
	if (out != null) {
	    synchronized (file) {
		out.flush();
	    }
	}
    }

    public void close() throws Exception {
	if (out != null) {
	    synchronized (file) {
		try {
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    public void writeLine(String line) throws IOException {
	if (out != null) {
	    synchronized (file) {
		write(line + "\n");
	    }
	}
    }

    public void write(String content) throws IOException {
	if (out != null) {
	    synchronized (file) {
		out.write(content);
	    }
	}
    }

    public void write(Number value) throws IOException {
	if (out != null) {
	    synchronized (file) {
		out.write(String.valueOf(value));
	    }
	}
    }
}
