package com.example.tools.commandline;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by THINK on 2017/1/25.
 */
public class Commands extends Excutable<Integer> {

    private File workDir;
    public Commands() {

    }
    public Commands(File workDir) {
        this.workDir = workDir;
    }

    @Override
    protected File getWorkDir() {
        return workDir;
    }

    public SimpleHandler createHandler(Process process) {
        return new SimpleHandler();
    }

    protected Charset charset() {
        return Charset.forName("UTF-8");
    }



    public static void main(String[] args) throws Exception {

        Commands commands = new Commands();
        Integer ping = commands.getResult("echo","www.qq.com");
        System.out.println(">>"+ping);
    }
}
