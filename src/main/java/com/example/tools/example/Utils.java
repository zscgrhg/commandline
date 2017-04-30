package com.example.tools.example;


import com.example.tools.commandline.Commands;

import java.io.File;

/**
 * Created by think on 17-4-29.
 */
public class Utils {
    static final String user_home = System.getProperty("user.home");

    public static File getDir(File baseDir, String dirnameRegex) throws Exception {
        File[] files = baseDir.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().matches(dirnameRegex)) {
                return file;
            }
        }
        throw new Exception("dir not exsit: " + dirnameRegex);
    }

    public static File getFile(File baseDir, String filenameRegex) throws Exception {
        File[] files = baseDir.listFiles();
        for (File file : files) {
            if (!file.isDirectory() && file.getName().matches(filenameRegex)) {
                return file;
            }
        }
        throw new Exception("dir not exsit: " + filenameRegex);
    }

    public static void untar(File workDir, File tarFile) throws Exception {
        untar(workDir, tarFile, workDir);
    }

    public static void untar(File workDir, File tarFile, File dest) throws Exception {

        if (!dest.exists()) {
            dest.mkdirs();
        }
        Commands commands = new Commands();
        commands.execute(workDir, "tar", "-zxvf", tarFile.getCanonicalPath(), "-C", dest.getCanonicalPath());
    }
}
