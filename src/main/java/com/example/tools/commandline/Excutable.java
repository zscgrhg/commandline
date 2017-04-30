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

    protected File getWorkDir() {
        return null;
    }

    protected void initProcessBuilder(ProcessBuilder pb) throws IOException {
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
    }

    protected ProcessBuilder createProcessBuilder(List<String> args) throws IOException {
        List<String> cmds = new ArrayList<String>();
        cmds.addAll(getCommandLines(args));
        ProcessBuilder pb = new ProcessBuilder(cmds);
        File workDir = getWorkDir();
        if (null != workDir && workDir.exists() && workDir.isDirectory()) {
            pb.directory(workDir);
        }
        return pb;
    }

    public R excute(String... args) throws Exception {
        return excute(Arrays.asList(args));
    }

    public R excute(List<String> args) throws Exception {
        return excuteAndRedirectToFiles(null, null, null, args);
    }

    public R excute(File out, String... args) throws Exception {
        return excuteAndRedirectToFiles(null, out, null, Arrays.asList(args));
    }

    public R excuteAndRedirectToFiles(File in, File out, File err, String... args) throws Exception {
        return excuteAndRedirectToFiles(in, out, err, Arrays.asList(args));
    }

    public R excuteAndRedirectToFiles(File in, File out, File err, List<String> args) throws Exception {
        ProcessBuilder pb = createProcessBuilder(args);

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
        Handler<R> handler = createHandler(process);
        handler.onStart();
        try {
            waitUntilProcessExit(process);
        } finally {
            killIfAlive(process);
            handler.onComplete(process.exitValue());
        }
        return handler.get();
    }

    public R getFineResult(List<String> args) throws Exception {
        ProcessBuilder pb = createProcessBuilder(args);
        initProcessBuilder(pb);
        Process process = pb.start();
        Handler<R> handler = createHandler(process);
        handler.onStart();
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

    public R getFineResult(String... args) throws Exception {

        return getFineResult(Arrays.asList(args));
    }


    public void killIfAlive(Process process) {
        try {
            process.exitValue();
        } catch (IllegalThreadStateException e) {
            process.destroy();
        }
    }
}
