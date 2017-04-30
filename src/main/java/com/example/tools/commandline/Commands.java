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
public  class Commands {


    protected Charset charset() {
        return Charset.forName("UTF8");
    }

    protected List<String> getCommandLines(List<String> args) {
        return args;
    }

    protected void waitUntilProcessExit(Process process) throws Exception {
        process.waitFor();
    }



    protected void initProcessBuilder(ProcessBuilder pb) throws IOException {
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb.redirectErrorStream(true);
    }

    protected ProcessBuilder createProcessBuilder(List<String> args) throws IOException {
        List<String> cmds = new ArrayList<>();
        cmds.addAll(getCommandLines(args));
        ProcessBuilder pb = new ProcessBuilder(cmds);
        return pb;
    }

    public int execute(File workDir, String... args) throws Exception {
        return execute(workDir, Arrays.asList(args));
    }

    public int execute(File workDir, List<String> args) throws Exception {
        return executeAndRedirects(workDir, null, null, null, args);
    }

    public int execute(File workDir, File out, String... args) throws Exception {
        return executeAndRedirects(workDir, null, out, null, Arrays.asList(args));
    }

    public int executeAndRedirects(File workDir, File in, File out, File err, String... args) throws Exception {
        return executeAndRedirects(workDir, in, out, err, Arrays.asList(args));
    }

    public int executeAndRedirects(File workDir, File in, File out, File err, List<String> args) throws Exception {
        ProcessBuilder pb = createProcessBuilder(args);
        if(workDir!=null){
            pb.directory(workDir);
        }
        initProcessBuilder(pb);
        if (null != in) {
            pb.redirectInput(in);
        }

        if (null != out) {
            pb.redirectOutput(out);
        } else {
            if (ProcessBuilder.Redirect.PIPE.equals(pb.redirectOutput())) {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            }
        }
        if (null != err) {
            pb.redirectErrorStream(false);
            pb.redirectError(err);
        } else {
            if (!pb.redirectErrorStream() &&
                    (ProcessBuilder.Redirect.PIPE.equals(pb.redirectError()))) {
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            }
        }
        Process process = pb.start();

        try {
            waitUntilProcessExit(process);
        } finally {
            killIfAlive(process);
        }
        return process.exitValue();
    }

    public <R> R excuteHandler(File workDir, Handler<R> handler, List<String> args) throws Exception {
        ProcessBuilder pb = createProcessBuilder(args);
        initProcessBuilder(pb);
        if(workDir!=null){
            pb.directory(workDir);
        }
        Process process = pb.start();
        handler.onStart(process);
        ProcessReader stderrReader=null;
        if(pb.redirectErrorStream()){
            InputStream errorStream = process.getErrorStream();
            stderrReader =
                    new ProcessReader(false, errorStream, charset(), handler);
            stderrReader.start();
        }
        InputStream inputStream = process.getInputStream();
        ProcessReader stdoutReader =
                new ProcessReader(inputStream, charset(), handler);
        stdoutReader.start();
        try {
            waitUntilProcessExit(process);
        } finally {
            killIfAlive(process);
            stdoutReader.join();
            if(null!=stderrReader){
                stderrReader.join();
            }
            handler.onComplete(process.exitValue());
        }
        return handler.get();
    }

    public <R> R excuteHandler(File workDir, Handler<R> handler, String... args) throws Exception {

        return excuteHandler(workDir,handler,Arrays.asList(args));
    }


    public <R> Thread excuteHandlerAsync(final File workDir, final Handler<R> handler, final String... args) throws Exception {
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    excuteHandler(workDir,handler,args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();
        return t;
    }

    public void killIfAlive(Process process) {
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            process.destroy();
        }
    }
}
