package com.cc.jcg;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class SwingProgressBar {

    private final JFrame main;
    private final JProgressBar progressBar;
    private int max;
    private int delay;

    public SwingProgressBar(String title) {
	this(title, 600, "Calibri", 10);
    }

    public SwingProgressBar(String title, int width, String fontName, int fontSize) {
	super();
	setUIFont(new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 10));
	main = new JFrame(title);
	main.setSize(width, 100);
	main.setLayout(new GridLayout(1, 1));
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	main.setLocation(dim.width / 2 - main.getSize().width / 2, dim.height / 2 - main.getSize().height / 2);
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	main.add(panel);
	main.setVisible(false);
	max = 100;
	progressBar = new JProgressBar(0, 100);
	progressBar.setStringPainted(true);
	progressBar.setMaximum(max);
	Dimension prefSize = progressBar.getPreferredSize();
	prefSize.width = width - 50;
	progressBar.setPreferredSize(prefSize);
	panel.add(progressBar);
	progressBar.setValue(0);
	main.pack();
	delay = 0;
    }

    public final synchronized int getDelay() {
	return delay;
    }

    public final synchronized void setDelay(int delay) {
	this.delay = delay;
    }

    public final synchronized int getMax() {
	return max;
    }

    public final synchronized void setMax(int max) {
	progressBar.setMaximum(max);
	progressBar.setValue(0);
	this.max = max;
    }

    public synchronized void start() {
	main.setVisible(true);
    }

    public synchronized void update(int progress, String text) {
	progressBar.setValue(progress);
	progressBar.setString(text);
	if (delay > 0) {
	    try {
		Thread.sleep(delay);
	    } catch (InterruptedException e) {
	    }
	}
    }

    public synchronized void update(String text) {
	int next = progressBar.getValue() + 1;
	next = next % (progressBar.getMaximum() + 1);
	progressBar.setValue(next);
	progressBar.setString(text);
	if (delay > 0) {
	    try {
		Thread.sleep(delay);
	    } catch (InterruptedException e) {
	    }
	}
    }

    public synchronized void stop() {
	update(getMax(), "finished");
	if (delay > 0) {
	    try {
		Thread.sleep(delay);
	    } catch (InterruptedException e) {
	    }
	}
	main.setVisible(false);
	main.dispose();
    }

    private static void setUIFont(javax.swing.plaf.FontUIResource f) {
	java.util.Enumeration keys = UIManager.getDefaults().keys();
	while (keys.hasMoreElements()) {
	    Object key = keys.nextElement();
	    Object value = UIManager.get(key);
	    if (value != null && value instanceof javax.swing.plaf.FontUIResource) {
		UIManager.put(key, f);
	    }
	}
    }
}
