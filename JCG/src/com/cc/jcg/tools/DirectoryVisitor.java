package com.cc.jcg.tools;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class DirectoryVisitor
	implements Callable<File> {

    private final File directory;
    private final AtomicInteger level;

    public DirectoryVisitor(File directory) {
	super();
	this.directory = directory;
	level = new AtomicInteger(0);
    }

    @Override
    public File call() throws Exception {
	level.set(0);
	traverseDirectory(directory);
	return directory;
    }

    protected final int getCurrentLevel() {
	return level.get();
    }

    protected final void traverseDirectory(File directory) throws Exception {
	if (!directory.exists()) {
	    throw new Exception("could not find directory " + directory.getPath());
	}
	for (File file : listFiles(directory)) {
	    if (file.isDirectory()) {
		level.incrementAndGet();
		try {
		    onDirectory(file);
		} catch (Exception e) {
		    onError(file, e);
		} finally {
		    level.decrementAndGet();
		}
	    } else {
		level.incrementAndGet();
		try {
		    onFile(file);
		} catch (Exception e) {
		    onError(file, e);
		} finally {
		    level.decrementAndGet();
		}
	    }
	}
    }

    protected File[] listFiles(File dir) {
	return dir.listFiles();
    }

    protected void onError(File file, Exception e) throws Exception {
	e.printStackTrace();
	throw e;
    }

    protected abstract void onFile(File file) throws Exception;

    protected abstract void onDirectory(File dir) throws Exception;
}
