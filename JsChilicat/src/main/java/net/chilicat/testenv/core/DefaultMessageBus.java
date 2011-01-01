package net.chilicat.testenv.core;

import net.chilicat.testenv.util.AbstractMessageBus;

import java.io.PrintStream;

/**
 */
public class DefaultMessageBus extends AbstractMessageBus {
    private final PrintStream out;

    public DefaultMessageBus(PrintStream out) {
        this.out = out;
    }

    public DefaultMessageBus() {
        this(System.out);
    }

    @Override
    public void print(String message) {
        out.print(message);
    }
}
