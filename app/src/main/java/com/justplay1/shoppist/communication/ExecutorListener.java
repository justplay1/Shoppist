package com.justplay1.shoppist.communication;

/**
 * Created by Misha on 06.09.2014.
 */
public interface ExecutorListener<R> {

    public void start();
    public void complete(R result);
    public void error(Exception e);
}
