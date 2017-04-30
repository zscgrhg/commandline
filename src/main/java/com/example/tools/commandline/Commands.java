package com.example.tools.commandline;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created by THINK on 2017/1/25.
 */
public class Commands extends Excutable {



    public Commands() {

    }



    public static void main(String[] args) throws Exception {

        Commands commands = new Commands();
        Integer ping = commands.excuteInAndRedirectToFiles(new File("."),null, null, null, "ping", "www.qq.com");
        System.out.println(">>" + ping);
    }



    protected Charset charset() {
        return Charset.forName("UTF-8");
    }
}
