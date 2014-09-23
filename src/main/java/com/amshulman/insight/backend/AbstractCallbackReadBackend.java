package com.amshulman.insight.backend;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import com.amshulman.insight.query.QueryParameterBuilder;

@RequiredArgsConstructor
public abstract class AbstractCallbackReadBackend implements AutoCloseable {

    protected static final ExecutorService threadPool = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(100));
    protected final ReadBackend readBackend;

    public final QueryParameterBuilder newQueryBuilder() {
        return readBackend.newQueryBuilder();
    }

    @Override
    public final void close() {
        readBackend.close();
    }
}
