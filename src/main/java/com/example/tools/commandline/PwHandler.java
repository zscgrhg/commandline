package com.example.tools.commandline;

import java.io.PrintStream;

/**
 * Created by THINK on 2017/2/28.
 */
public class PwHandler extends ExitValueHandler {
    private final PrintStream out;

    public PwHandler() {
        this.out = System.out;
    }

    public PwHandler(PrintStream out) {
        this.out = out;
    }

    @Override
    public void receiveError(String line) {

        if (null != out) {
            out.println(line);
        }
    }

    @Override
    public void receive(String line) {

        if (null != out) {
            out.println(line);
        }
    }
}
