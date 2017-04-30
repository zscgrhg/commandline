package com.example.tools.example;

import com.example.tools.commandline.Commands;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CountDownLatch;

/**
 * Created by root on 17-4-30.
 */
public class RedisTest {

    public static void startRedis() throws Exception {
        final Commands commands = new Commands();
        File clusterBasedir = new File("/root/IdeaProjects/commandline/redis");
        commands.excuteIn(clusterBasedir,"/bin/bash","./shutdown.sh");


        final Path conf = Paths.get("/root/IdeaProjects/commandline/src/main/resources/redis.conf");

        File[] files = clusterBasedir.listFiles();
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (final File file : files) {
            if (file.isFile()) {
                continue;
            }
            try {
                Path resolve = file.toPath().resolve("redis.conf");
                if (!resolve.toFile().exists()) {
                    Files.copy(conf, resolve, StandardCopyOption.REPLACE_EXISTING);
                }
                File[] toDelete = file.listFiles();
                for (File f : toDelete) {
                    if(!f.getName().equalsIgnoreCase("redis.conf")){
                        f.delete();
                    }
                }


                File log = file.toPath().resolve("redis.log").toFile();
                commands.excuteIn(file, "ls");
                commands.excuteHandlerAsync(file, new RedisStartHandler(countDownLatch,log), "/home/think/test-redis/redis-3.2.8/src/redis-server", "./redis.conf");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        countDownLatch.await();
        commands.excuteInAndRedirectToFiles(clusterBasedir.toPath().resolve("7000").toFile(),
                clusterBasedir.toPath().resolve("yes.txt").toFile(),
                null,
                null, "/home/think/test-redis/redis-3.2.8/src/redis-trib.rb",
                "create", "--replicas", "1"
                , "127.0.0.1:7000"
                , "127.0.0.1:7001"
                , "127.0.0.1:7002"
                , "127.0.0.1:7003"
                , "127.0.0.1:7004"
                , "127.0.0.1:7005"
        );
    }

    public static void main(String[] args) throws Exception {
        startRedis();
        System.out.println("done");
    }
}
