package com.example.tools.commandline;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class FineCommand<R> extends Excutable<R> {
    @Override
    public Handler<R> createHandler(Process process) {
        ThreadHandler<R> threadHandler = new ThreadHandler<R>(fineHandler(process));
        threadHandler.start();
        return threadHandler;
    }

    protected abstract Handler<R> fineHandler(Process process);
}
