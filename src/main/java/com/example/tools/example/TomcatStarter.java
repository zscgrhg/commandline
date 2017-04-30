package com.example.tools.example;


import com.example.tools.commandline.Commands;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static com.example.tools.example.Utils.*;

/**
 * Created by root on 17-4-30.
 */
public class TomcatStarter {
    public static final File tomcat_home = new File("/root/IdeaProjects/commandline/tomcat_cluster/");


    public static void shutdownTomcat() throws Exception {
        Commands commands = new Commands();
        commands.execute(null, "/bin/bash", "./shutdown-tomcat.sh");
    }

    public static void startTomcat(int size) throws Exception {
        File tar = getFile(tomcat_home, "redisson-apache-tomcat.*");
        Commands commands = new Commands();
        String[] ports = new String[size];
        for (int i = 0; i < size; i++) {
            ports[i] = "8080".replaceAll("8(?=\\d)", String.valueOf(5 + i));
        }
        for (String port : ports) {
            File deploy = tomcat_home.toPath().resolve(port).toFile();
            if (!deploy.exists()) {
                deploy.mkdir();
                untar(tomcat_home, tar, deploy);
            }
            File tc_home = getDir(deploy, "apache-tomcat.*");
            File logs = tc_home.toPath().resolve("logs").toFile();
            for (File log : logs.listFiles()) {
                log.delete();
            }


            File server = tc_home.toPath().resolve("conf/server.xml").toFile();

            byte[] bytes = Files.readAllBytes(server.toPath());
            String string = new String(bytes, "utf-8");
            String conf = string.replaceAll("8(?=\\d)", String.valueOf(port.charAt(0)));
            Files.copy(new ByteArrayInputStream(conf.getBytes("utf-8")), server.toPath(), StandardCopyOption.REPLACE_EXISTING);
            commands.execute(tc_home.toPath().resolve("bin").toFile(), "./startup.sh");
        }
    }


    public static void main(String[] args) throws Exception {
        shutdownTomcat();
        startTomcat(3);
    }
}
