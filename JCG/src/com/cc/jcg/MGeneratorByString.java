package com.cc.jcg;

public class MGeneratorByString {

    private final MPackage pckg;
    private final String name;

    public MGeneratorByString(MPackage pckg, String name) {
	super();
	this.pckg = pckg;
	this.name = name;
    }

    public MClass generateClass(boolean doNotGenerate) {
	final MClass cls;
	if (name.contains(".")) {
	    String exName = name.split("\\.")[name.split("\\.").length - 1];
	    String exPackgName = name.substring(0, name.length() - exName.length() - 1);
	    MPackage exPackg = pckg.getBundle().getPackageByName(exPackgName);
	    if (exPackg == null) {
		exPackg = pckg.getBundle().newPackage(exPackgName);
	    }
	    if (exPackg.getClassByName(exName) == null) {
		cls = exPackg.newClass(exName);
		if (doNotGenerate) {
		    cls.doNotGenerate();
		}
	    } else {
		cls = exPackg.getClassByName(exName);
	    }
	} else {
	    if (pckg.getClassByName(name) == null) {
		cls = pckg.newClass(name);
		if (doNotGenerate) {
		    cls.doNotGenerate();
		}
	    } else {
		cls = pckg.getClassByName(name);
	    }
	}
	return cls;
    }

    public MInterface generateInterface(boolean doNotGenerate) {
	final MInterface intf;
	if (name.contains(".")) {
	    String exName = name.split("\\.")[name.split("\\.").length - 1];
	    String exPackgName = name.substring(0, name.length() - exName.length() - 1);
	    MPackage exPackg = pckg.getBundle().getPackageByName(exPackgName);
	    if (exPackg == null) {
		exPackg = pckg.getBundle().newPackage(exPackgName);
	    }
	    if (exPackg.getInterfaceByName(exName) == null) {
		intf = exPackg.newInterface(exName);
		if (doNotGenerate) {
		    intf.doNotGenerate();
		}
	    } else {
		intf = exPackg.getInterfaceByName(exName);
	    }
	} else {
	    if (pckg.getInterfaceByName(name) == null) {
		intf = pckg.newInterface(name);
		if (doNotGenerate) {
		    intf.doNotGenerate();
		}
	    } else {
		intf = pckg.getInterfaceByName(name);
	    }
	}
	return intf;
    }
}
