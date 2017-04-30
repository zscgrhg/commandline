package com.example.tools.commandline;

import java.util.Properties;

/**
 * Created by root on 17-4-30.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println(System.getProperty("os.arch"));
        Properties properties = System.getProperties();
        properties.list(System.out);
    }
}
