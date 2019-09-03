package com.cc.jcg.tools;

import java.io.File;

// alias of FileVisitor
public abstract class FileReader
	extends FileVisitor {

    public FileReader(File file) {
	super(file);
    }
}
