package com.cc.jcg.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class EncodedFileReader
	implements Callable<File> {

    public static final String DEFAULT_ENCODING = "UTF8";
    public static final int DEFAULT_BUFFER = 8192;
    private final File file;
    private final String encoding;
    protected final AtomicBoolean stop;
    protected final AtomicLong lineNumber;
    private final int buffer;

    public EncodedFileReader(File file) {
	this(file, DEFAULT_BUFFER);
    }

    public EncodedFileReader(File file, String encoding) {
	this(file, encoding, DEFAULT_BUFFER);
    }

    public EncodedFileReader(File file, int buffer) {
	this(file, DEFAULT_ENCODING, buffer);
    }

    public EncodedFileReader(File file, String encoding, int buffer) {
	super();
	this.buffer = buffer;
	this.file = file;
	this.encoding = encoding;
	stop = new AtomicBoolean(false);
	lineNumber = new AtomicLong();
    }

    @Override
    public File call() throws Exception {
	BufferedReader in = null;
	try {
	    in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding), buffer);
	    String line = null;
	    while (!stop.get() && (line = in.readLine()) != null) {
		lineNumber.incrementAndGet();
		onLine(line);
	    }
	    onEndOfFile();
	} catch (IOException e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    if (in != null) {
		try {
		    in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return file;
    }

    protected void onEndOfFile() {
    }

    protected abstract void onLine(String line) throws Exception;
}
