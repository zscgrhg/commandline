package com.example.tools.commandline;

/**
 * Created by THINK on 2017/2/4.
 */
public abstract class FineCommand<R> extends Excutable<R> {
    @Override
    public Handler<R> createHandler(Process process) {
        HandlerThread<R> handlerThread = new HandlerThread<R>(fineHandler(process));
        handlerThread.start();
        return handlerThread;
    }

    protected abstract Handler<R> fineHandler(Process process);
}
