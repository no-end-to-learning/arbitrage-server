package me.qiujun.arbitrage.adapter.bybit.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.WebSocketClientWrapper;
import me.qiujun.arbitrage.adapter.bybit.BybitWebSocketCallback;
import me.qiujun.arbitrage.adapter.bybit.bean.event.BybitEvent;
import okhttp3.*;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.*;

@Slf4j
public class BybitWebSocketClientImpl {

    private final OkHttpClient client;

    private final String baseUrl;

    private final String apiKey;

    private final String secretKey;

    public BybitWebSocketClientImpl(OkHttpClient client, String baseUrl, String apiKey, String secretKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    protected <T> Closeable createConnection(
            String path,
            List<String> topics,
            BybitWebSocketCallback<BybitEvent<T>> callback,
            TypeReference<BybitEvent<T>> type
    ) {
        String streamingUrl = this.baseUrl + path;
        Request request = new Request.Builder().url(streamingUrl).build();

        return new WebSocketClientWrapper(client, request, new WebSocketListener() {

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                BybitWebSocketClientImpl self = BybitWebSocketClientImpl.this;

                // 发送鉴权
                if (path.contains("/private/")) {
                    String expires = String.valueOf(new Date().getTime() + 10_000L);
                    String signatureStr = "GET/realtime" + expires;
                    String signature = (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, self.secretKey)).hmacHex(signatureStr);
                    Map<String, Object> authPayload = new HashMap<>();
                    authPayload.put("op", "auth");
                    authPayload.put("args", Arrays.asList(self.apiKey, expires, signature));
                    webSocket.send(JSON.toJSONString(authPayload));
                }

                // 订阅 Topic
                Map<String, Object> subscribePayload = new HashMap<>();
                subscribePayload.put("op", "subscribe");
                subscribePayload.put("args", topics);
                webSocket.send(JSON.toJSONString(subscribePayload));

            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                JSONObject jsonObject = JSON.parseObject(text);
                String topic = jsonObject.getString("topic");
                if (topic == null || !topics.contains(topic)) {
                    log.warn("unknown message {}", text);
                    return;
                }

                callback.onMessage(JSON.parseObject(text, type));
            }

        });
    }

}
