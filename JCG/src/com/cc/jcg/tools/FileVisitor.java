package com.cc.jcg.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Callable;

public abstract class FileVisitor
	implements Callable<File> {

    private final File file;

    public FileVisitor(File file) {
	super();
	this.file = file;
    }

    public File call() throws Exception {
	BufferedReader in = null;
	try {
	    in = new BufferedReader(new FileReader(file));
	    String line = null;
	    while ((line = in.readLine()) != null) {
		onLine(line);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    if (in != null) {
		in.close();
	    }
	}
	return file;
    }

    protected abstract void onLine(String line) throws Exception;
}
