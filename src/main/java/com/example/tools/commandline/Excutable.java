package com.example.tools.commandline;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class Excutable<R> {
    protected Charset stdoutCharset() {
        return charset();
    }

    protected Charset charset() {
        return Charset.forName("UTF8");
    }

    protected Charset stderrCharset() {
        return charset();
    }

    public abstract Handler<R> createHandler(Process process);

    protected List<String> getCommandLines(List<String> args) {
        return args;
    }

    protected void waitUntilProcessExit(Process process) throws Exception {
        process.waitFor();
    }
    protected File getWorkDir(){
        return null;
    }
    protected Process createProcess(String... args) throws IOException {
       return createProcess(Arrays.asList(args));
    }
    protected Process createProcess(List<String> args) throws IOException {
        List<String> cmds = new ArrayList<String>();
        cmds.addAll(getCommandLines(args));
        ProcessBuilder pb = new ProcessBuilder(cmds);
        File workDir = getWorkDir();
        if(null!= workDir&&workDir.exists()&&workDir.isDirectory()){
            pb.directory(workDir);
        }
        return pb.start();
    }
    public R excute(List<String> args) throws Exception{
        Process process = createProcess(args);
        process.getOutputStream().close();
        InputStream errorStream = process.getErrorStream();
        Handler<R> handler = createHandler(process);
        ProcessReader stderrReader =
                new ProcessReader(false, errorStream, stderrCharset(), handler);
        stderrReader.start();
        InputStream inputStream = process.getInputStream();
        ProcessReader stdoutReader =
                new ProcessReader(inputStream, stdoutCharset(), handler);
        stdoutReader.start();
        try {
            waitUntilProcessExit(process);
        } finally {
            killIfAlive(process);
            stdoutReader.join();
            stderrReader.join();
            handler.onComplete(process.exitValue());
        }
        return handler.get();
    }
    public R excute(String... args) throws Exception {

        return excute(Arrays.asList(args));
    }

    public void killIfAlive(Process process) {
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            process.destroy();
        }
    }
}
