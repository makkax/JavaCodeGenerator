package com.cc.jcg;

public class BlockContentBuffer {

    private final StringBuffer sb = new StringBuffer();

    @Override
    public String toString() {
	return sb.toString();
    }

    public void append(String s) {
	sb.append(s);
    }

    public void line(String s) {
	append(s);
	sb.append("\n");
    }

    public void append(String s, String par0) {
	sb.append(s.replace("{0}", par0));
    }

    public void line(String s, String par0) {
	append(s, par0);
	sb.append("\n");
    }

    public void append(String s, String par0, String par1) {
	sb.append(s.replace("{0}", par0).replace("{1}", par1));
    }

    public void line(String s, String par0, String par1) {
	append(s, par0, par1);
	sb.append("\n");
    }

    public void append(String s, String par0, String par1, String par2) {
	sb.append(s.replace("{0}", par0).replace("{1}", par1).replace("{2}", par2));
    }

    public void line(String s, String par0, String par1, String par2) {
	append(s, par0, par1, par2);
	sb.append("\n");
    }

    public void append(String s, String par0, String par1, String par2, String par3) {
	sb.append(s.replace("{0}", par0).replace("{1}", par1).replace("{2}", par2).replace("{3}", par3));
    }

    public void line(String s, String par0, String par1, String par2, String par3) {
	append(s, par0, par1, par2, par3);
	sb.append("\n");
    }
}
