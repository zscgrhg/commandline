package com.example.tools.example;

import com.example.tools.commandline.SimpleHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Created by root on 17-4-30.
 */
public class RedisStartHandler extends SimpleHandler {

    final CountDownLatch latch;
    final FileWriter log;

    public RedisStartHandler(CountDownLatch latch, File writer) throws IOException {
        this.latch = latch;
        this.log = new FileWriter(writer);
    }


    @Override
    public void onComplete(int processExitValue) {
        super.onComplete(processExitValue);
        try {
            log.close();
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
}
