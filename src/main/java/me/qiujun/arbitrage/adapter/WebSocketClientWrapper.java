package me.qiujun.arbitrage.adapter;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class WebSocketClientWrapper implements WebSocket, Closeable {

    private final OkHttpClient originalOkHttpClient;

    private final Request originalRequest;

    private final WebSocketListener originalListener;

    private WebSocket webSocket;

    private Timer reconnectTimer = null;

    private final AtomicInteger reconnectAttemptCount = new AtomicInteger(0);

    private final AtomicBoolean isConnected = new AtomicBoolean(false);

    private final AtomicBoolean isConnecting = new AtomicBoolean(false);

    private final AtomicBoolean isManualClosed = new AtomicBoolean(false);

    private final int MAX_RECONNECT_ATTEMPT_COUNT = 10;

    private final long RECONNECT_DELAY = 0L;

    private final long RECONNECT_PERIOD = 3000L;

    private final WebSocketListener webSocketListener = new WebSocketListener() {

        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            isConnected.compareAndSet(true, false);
            if (!isManualClosed.get()) {
                doReconnect();
            }
            originalListener.onClosed(webSocket, code, reason);
            log.info("websocket connection {} closed", webSocket.request().url());
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            originalListener.onClosing(webSocket, code, reason);
            log.info("websocket connection {} closing", webSocket.request().url());
        }

        @Override
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
            isConnected.compareAndSet(true, false);
            if (!isManualClosed.get()) {
                doReconnect();
            }
            originalListener.onFailure(webSocket, t, response);
            log.info("websocket connection {} error {}", webSocket.request().url(), ExceptionUtils.getStackTrace(t));
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            originalListener.onMessage(webSocket, text);
        }

        @Override
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            originalListener.onMessage(webSocket, bytes);
        }

        @Override
        public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
            isConnected.compareAndSet(false, true);
            isConnecting.compareAndSet(true, false);

            synchronized (WebSocketClientWrapper.this) {
                if (reconnectTimer != null) {
                    reconnectTimer.cancel();
                    reconnectTimer = null;
                }
            }
            reconnectAttemptCount.set(0);
            originalListener.onOpen(webSocket, response);
            log.info("websocket connected to {}", webSocket.request().url());
        }

    };


    public WebSocketClientWrapper(OkHttpClient okHttpClient, Request request, WebSocketListener listener) {
        this.originalOkHttpClient = okHttpClient;
        this.originalRequest = request;
        this.originalListener = listener;
        webSocket = okHttpClient.newWebSocket(request, webSocketListener);
    }

    private void doReconnect() {
        if (isConnected.get() || isConnecting.get()) {
            return;
        }

        isConnecting.compareAndSet(false, true);

        synchronized (WebSocketClientWrapper.this) {
            if (reconnectTimer == null) {
                reconnectTimer = new Timer();
            }

            reconnectTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    log.info("try reconnect websocket to {}", originalRequest.url());
                    if (reconnectAttemptCount.get() > MAX_RECONNECT_ATTEMPT_COUNT) {
                        log.error("reconnect attempt > reconnectCount, it won't be to reconnect");
                    }
                    webSocket.cancel();
                    webSocket = originalOkHttpClient.newWebSocket(originalRequest, webSocketListener);
                }
            }, RECONNECT_DELAY, RECONNECT_PERIOD);
        }
    }

    @Override
    public void cancel() {
        isConnected.compareAndSet(true, false);
        if (reconnectTimer != null) {
            reconnectTimer.cancel();
            reconnectTimer = null;
        }
        webSocket.cancel();
    }

    @Override
    public void close() {
        isManualClosed.compareAndSet(false, true);
        webSocket.close(1000, "Bye");
    }

    @Override
    public boolean close(int code, @Nullable String reason) {
        return webSocket.close(code, reason);
    }

    @Override
    public long queueSize() {
        return webSocket.queueSize();
    }

    @NotNull
    @Override
    public Request request() {
        return webSocket.request();
    }

    @Override
    public boolean send(@NotNull String text) {
        return webSocket.send(text);
    }

    @Override
    public boolean send(@NotNull ByteString bytes) {
        return webSocket.send(bytes);
    }

}
