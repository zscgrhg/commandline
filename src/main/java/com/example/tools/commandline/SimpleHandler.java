package com.example.tools.commandline;

/**
 * Created by root on 17-4-30.
 */
public class SimpleHandler implements Handler<Integer> {
    private volatile int exitValue=0;
    @Override
    public void onStart(Process process) {

    }

    @Override
    public void onComplete(int processExitValue) {
        this.exitValue=processExitValue;
    }

    @Override
    public void onStderrEnd() {

    }

    @Override
    public void onStdoutEnd() {

    }

    @Override
    public void receiveError(String line) {

    }

    @Override
    public void receive(String line) {

    }

    @Override
    public Integer get() {
        return exitValue;
    }
}
