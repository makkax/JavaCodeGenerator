package com.cc.jcg.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public final class BufferedCopy
	implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int bufferSize;

    public BufferedCopy() {
	this(1024);
    }

    public BufferedCopy(int bufferSize) {
	super();
	this.bufferSize = bufferSize;
    }

    public void copy(File input, File output) throws Exception {
	copy(new FileInputStream(input), new FileOutputStream(output));
    }

    public void copy(InputStream is, OutputStream os) throws Exception {
	BufferedInputStream bis = null;
	BufferedOutputStream bos = null;
	try {
	    bis = new BufferedInputStream(is, bufferSize);
	    bos = new BufferedOutputStream(os, bufferSize);
	    int buffer;
	    while ((buffer = bis.read()) != -1) {
		bos.write(buffer);
	    }
	    bos.flush();
	} catch (Exception e) {
	    e.printStackTrace();
	    throw e;
	} finally {
	    if (bis != null) {
		bis.close();
	    }
	    if (bos != null) {
		bos.close();
	    }
	}
    }
}
