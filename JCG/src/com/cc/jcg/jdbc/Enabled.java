package com.cc.jcg.jdbc;

public interface Enabled {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default void enable() {
	setEnabled(true);
    };

    default void disable() {
	setEnabled(false);
    }
}
