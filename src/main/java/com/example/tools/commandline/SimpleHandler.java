package com.example.tools.commandline;

/**
 * Created by THINK on 2017/1/25.
 */
public class SimpleHandler implements Handler<Integer> {
    private volatile int exitValue = 0;

    @Override
    public void onStart() {

    }

    public void onComplete(int procesExitValue) {
        exitValue = procesExitValue;
    }

    public void onStderrEnd() {

    }

    public void onStdoutEnd() {

    }

    public void receiveError(String line) {

    }

    public void receive(String line) {

    }

    public Integer get() {
        return exitValue;
    }
}
