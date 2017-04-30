package com.example.tools.example;

import com.example.tools.commandline.Handler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by root on 17-4-30.
 */
public class RedisStartHandler implements Handler<Integer> {

    final CountDownLatch latch;
    int exitValue=0;
    final FileWriter log;

    public RedisStartHandler(CountDownLatch latch, File writer) throws IOException {
        this.latch = latch;
        this.log = new FileWriter(writer);
    }

    @Override
    public void onStart(Process process) {
        try {
            log.write("start");
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(int processExitValue) {
        exitValue=processExitValue;
        try {
            log.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStderrEnd() {

    }

    @Override
    public void onStdoutEnd() {

    }

    @Override
    public void receiveError(String line) {
        try {
            log.write(line);
            log.write("\n");
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(String line) {
        try {

            if(line.contains("The server is now ready to accept connections on port")){
                latch.countDown();
                System.out.println(line);
            }
            log.write(line);
            log.write("\n");
            log.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Integer get() {
        return exitValue;
    }
}
