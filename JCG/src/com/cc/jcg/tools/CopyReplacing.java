package com.cc.jcg.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

public class CopyReplacing
	implements Callable<File> {

    private final File input;
    private final File output;
    private final Map<String, String> map;

    public CopyReplacing(File input, File output, Map<String, String> map) {
	super();
	this.input = input;
	this.output = output;
	this.map = map;
    }

    public CopyReplacing(File input, File output, File propertiesFile) throws FileNotFoundException, IOException {
	super();
	this.input = input;
	this.output = output;
	map = new HashMap<String, String>();
	Properties properties = new Properties();
	properties.load(new FileInputStream(propertiesFile));
	loadProperties(properties);
    }

    public CopyReplacing(File input, File output, Properties properties) {
	super();
	this.input = input;
	this.output = output;
	map = new HashMap<String, String>();
	loadProperties(properties);
    }

    protected void loadProperties(Properties properties) {
	for (Object key : properties.keySet()) {
	    if (key instanceof String) {
		map.put((String) key, properties.getProperty((String) key));
	    }
	}
    }

    @Override
    public File call() throws Exception {
	new EncodedFileWriter(output) {

	    @Override
	    protected void writing() throws Exception {
		new EncodedFileReader(input) {

		    @Override
		    protected void onLine(String line) throws Exception {
			for (String key : map.keySet()) {
			    line = line.replace(key, map.get(key));
			}
			if (doWriteLine(line)) {
			    writeLine(line);
			} else {
			    String replacement = getLineReplacement();
			    if (replacement != null) {
				writeLine(replacement);
			    }
			}
		    }
		}.call();
	    }
	}.call();
	return output;
    }

    protected String getLineReplacement() {
	return null;
    }

    protected boolean doWriteLine(String line) {
	return true;
    }
}
