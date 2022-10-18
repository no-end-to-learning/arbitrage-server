package me.qiujun.arbitrage.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

@Slf4j
public class ExecutorUtil {

    private static final String TRACE_ID = "traceId";

    public static ExecutorService newNamedCachedThreadPool(String threadNamePrefix) {
        ThreadFactory namedThreadFactory = new CustomizableThreadFactory(threadNamePrefix);
        return Executors.newCachedThreadPool(namedThreadFactory);
    }

    public static Callable<Boolean> withName(Callable<Boolean> callable, String name) {
        return () -> {
            Thread currentThread = Thread.currentThread();

            String oldName = currentThread.getName();
            currentThread.setName(name);

            try {
                return callable.call();
            } finally {
                currentThread.setName(oldName);
            }
        };
    }

    public static Runnable withName(Runnable runnable, String name) {
        return () -> {
            Thread currentThread = Thread.currentThread();

            String oldName = currentThread.getName();
            currentThread.setName(name);

            try {
                runnable.run();
            } finally {
                currentThread.setName(oldName);
            }
        };
    }

    public static Callable<Boolean> withMDC(Callable<Boolean> callable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }

            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable withMDC(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            if (contextMap != null) {
                MDC.setContextMap(contextMap);
            }

            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Callable<Boolean> withTraceId(Callable<Boolean> callable) {
        return () -> {
            MDC.put(TRACE_ID, UUID.randomUUID().toString());

            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable withTraceId(Runnable runnable) {
        return () -> {
            MDC.put(TRACE_ID, UUID.randomUUID().toString());

            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }

}
