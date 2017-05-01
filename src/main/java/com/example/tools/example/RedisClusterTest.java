package com.example.tools.example;

import com.example.tools.commandline.Commands;

import java.io.File;

/**
 * Created by root on 17-4-30.
 */
public class RedisClusterTest {

    public static final File cluster_home = new File("/home/think/test-redis/redis-3.2.8/utils/create-cluster/");
    public static final File redis_script_file = cluster_home.toPath().resolve("create-cluster").toFile();
    static final Commands commands = new Commands();

    public static void stopCluster() throws Exception {
        commands.execute(cluster_home,
                redis_script_file.getCanonicalPath(), "stop");
        commands.execute(cluster_home,
                redis_script_file.getCanonicalPath(), "clean");
    }

    public static void startCluster() throws Exception {
        commands.execute(cluster_home,
                redis_script_file.getCanonicalPath(), "start");
        commands.executeRedirectIO(cluster_home,
                new File("/root/IdeaProjects/commandline/redis/yes.txt"),
                null, null, true,
                redis_script_file.getCanonicalPath(), "create");
    }

    public static void main(String[] args) throws Exception {
        stopCluster();
        //startCluster();
    }
}
