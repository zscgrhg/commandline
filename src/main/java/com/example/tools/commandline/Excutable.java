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
public abstract class Excutable {
    protected Charset stdoutCharset() {
        return charset();
    }

    protected Charset charset() {
        return Charset.forName("UTF8");
    }

    protected Charset stderrCharset() {
        return charset();
    }



    protected List<String> getCommandLines(List<String> args) {
        return args;
    }

    protected void waitUntilProcessExit(Process process) throws Exception {
        process.waitFor();
    }



    protected void initProcessBuilder(ProcessBuilder pb) throws IOException {
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
    }

    protected ProcessBuilder createProcessBuilder(List<String> args) throws IOException {
        List<String> cmds = new ArrayList<String>();
        cmds.addAll(getCommandLines(args));
        ProcessBuilder pb = new ProcessBuilder(cmds);
        return pb;
    }

    public int excuteIn(File workDir, String... args) throws Exception {
        return excuteIn(workDir,Arrays.asList(args));
    }

    public int excuteIn(File workDir, List<String> args) throws Exception {
        return excuteInAndRedirectToFiles(workDir,null, null, null, args);
    }

    public int excuteIn(File workDir, File out, String... args) throws Exception {
        return excuteInAndRedirectToFiles(workDir,null, out, null, Arrays.asList(args));
    }

    public int excuteInAndRedirectToFiles(File workDir, File in, File out, File err, String... args) throws Exception {
        return excuteInAndRedirectToFiles(workDir,in, out, err, Arrays.asList(args));
    }

    public int excuteInAndRedirectToFiles(File workDir, File in, File out, File err, List<String> args) throws Exception {
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
        InputStream errorStream = process.getErrorStream();
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
