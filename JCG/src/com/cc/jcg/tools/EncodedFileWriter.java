package com.cc.jcg.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;

public abstract class EncodedFileWriter
	implements Callable<File> {

    public static void generateWriting(File input) throws Exception {
	System.out.println("new EncodedFileWriter(output) {");
	System.out.println();
	System.out.println("    @Override");
	System.out.println("    protected void writing() throws Exception {");
	new EncodedFileReader(input) {

	    @Override
	    protected void onLine(String line) throws Exception {
		System.out.println("	writeLine(\"" + line.replace("\"", "\\\"") + "\");");
	    }
	}.call();
	System.out.println("    }");
	System.out.println("}.call();");
    }

    private final File file;
    private final String encoding;
    private BufferedWriter out;

    public EncodedFileWriter(File file) {
	this(file, "UTF8");
    }

    public EncodedFileWriter(File file, String encoding) {
	super();
	this.file = file;
	this.encoding = encoding;
    }

    @Override
    public final File call() throws Exception {
	out = null;
	try {
	    out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
	    synchronized (out) {
		writing();
	    }
	    out.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    if (out != null) {
		try {
		    out.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return file;
    }

    protected abstract void writing() throws Exception;

    protected void writeLine(String line) throws IOException {
	synchronized (out) {
	    write(line + "\n");
	}
    }

    protected void write(String content) throws IOException {
	synchronized (out) {
	    out.write(content);
	}
    }

    protected void write(Number value) throws IOException {
	synchronized (out) {
	    out.write(String.valueOf(value));
	}
    }
}
