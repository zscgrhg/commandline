package com.example.tools.commandline;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created by THINK on 2017/1/25.
 */
public class ProcessReader extends Thread {
    private final boolean isStdout;
    private final InputStream in;
    private final Charset charset;
    private final Handler handler;

    public ProcessReader(boolean isStdout, InputStream in, Charset charset, Handler handler) {
        this.isStdout = isStdout;
        this.in = in;
        this.charset = charset;
        this.handler = handler;
    }

    public ProcessReader(InputStream in, Charset charset, Handler handler) {
        this(true, in, charset, handler);
    }

    @Override
    public void run() {
        if (isStdout) {
            readStdout(in, charset.name());
        } else {
            readStderr(in, charset.name());
        }
    }

    private void readStderr(InputStream in, String charset) {
        try {
            Scanner scanner = new Scanner(in, charset);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                handler.receiveError(line);
            }
        } finally {
            handler.onStderrEnd();
        }

    }

    private void readStdout(InputStream in, String charset) {
        try {
            Scanner scanner = new Scanner(in, charset);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                handler.receive(line);
            }
        } finally {
            handler.onStdoutEnd();
        }

    }
}
