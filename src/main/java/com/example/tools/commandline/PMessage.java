package com.example.tools.commandline;

/**
 * Created by THINK on 2017/1/25.
 */
public class PMessage {
    public enum Key {
        START,STDERR, STDERR_END, STDOUT, STDOUT_END, FINISH
    }

    public final Key key;
    public final String line;

    public PMessage(Key key, String line) {
        this.key = key;
        this.line = line;
    }
}
