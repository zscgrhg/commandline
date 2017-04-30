package com.example.tools.commandline;

/**
 * Created by THINK on 2017/1/25.
 */
public interface Handler<R> {

    void onStart();

    void onComplete(int processExitValue);

    void onStderrEnd();

    void onStdoutEnd();

    void receiveError(String line);

    void receive(String line);

    R get();
}
