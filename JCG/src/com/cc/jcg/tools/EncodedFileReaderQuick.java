package com.cc.jcg.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public abstract class EncodedFileReaderQuick
	implements Callable<File> {

    public static final String DEFAULT_ENCODING = "UTF8";
    public static final int DEFAULT_BUFFER = 8192;
    private final File file;
    private final String encoding;
    private final int buffer;

    public EncodedFileReaderQuick(File file) {
	this(file, DEFAULT_BUFFER);
    }

    public EncodedFileReaderQuick(File file, String encoding) {
	this(file, encoding, DEFAULT_BUFFER);
    }

    public EncodedFileReaderQuick(File file, int buffer) {
	this(file, DEFAULT_ENCODING, buffer);
    }

    public EncodedFileReaderQuick(File file, String encoding, int buffer) {
	super();
	this.buffer = buffer;
	this.file = file;
	this.encoding = encoding;
    }

    @Override
    public File call() throws Exception {
	BufferedReader in = null;
	// Executor queue = Executors.newFixedThreadPool(4);
	try {
	    in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding), buffer);
	    String line = null;
	    while ((line = in.readLine()) != null) {
		if (!onLine(line)) {
		    break;
		}
	    }
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
	    onFinally();
	}
	return file;
    }

    protected abstract void onFinally();

    protected abstract boolean onLine(String line) throws Exception;
}
