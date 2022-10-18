package me.qiujun.arbitrage.adapter.gate.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.WebSocketClientWrapper;
import me.qiujun.arbitrage.adapter.gate.GateWebSocketCallback;
import me.qiujun.arbitrage.adapter.gate.bean.event.GateEvent;
import okhttp3.*;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class GateWebSocketClientImpl {

    private final OkHttpClient client;

    private final String baseUrl;

    private final String apiKey;

    private final String secretKey;

    public GateWebSocketClientImpl(OkHttpClient client, String baseUrl, String apiKey, String secretKey) {
        this.client = client;
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    protected <T> Closeable createConnection(
            Boolean isPrivate,
            String channel,
            Object payload,
            GateWebSocketCallback<GateEvent<T>> callback,
            TypeReference<GateEvent<T>> type
    ) {
        Request request = new Request.Builder().url(this.baseUrl + "/ws/v4/").build();
        return new WebSocketClientWrapper(client, request, new WebSocketListener() {

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                Long time = System.currentTimeMillis() / 1000;
                String event = "subscribe";

                Map<String, Object> request = new HashMap<>();
                request.put("time", time);
                request.put("channel", channel);
                request.put("event", event);
                request.put("payload", payload);

                // 发送鉴权
                if (isPrivate) {
                    GateWebSocketClientImpl self = GateWebSocketClientImpl.this;

                    String signatureStr = String.format("channel=%s&event=%s&time=%s", channel, event, time);
                    String signature = (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, self.secretKey)).hmacHex(signatureStr);

                    Map<String, String> requestAuth = new HashMap<>();
                    requestAuth.put("method", "api_key");
                    requestAuth.put("KEY", self.apiKey);
                    requestAuth.put("SIGN", signature);

                    request.put("auth", requestAuth);
                }

                // 订阅 Topic
                webSocket.send(JSON.toJSONString(request));
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                JSONObject jsonObject = JSON.parseObject(text);
                String messageChannel = jsonObject.getString("channel");
                String messageEvent = jsonObject.getString("event");
                if (!Objects.equals(messageChannel, channel) || !Objects.equals(messageEvent, "update")) {
                    log.warn("unknown message {}", text);
                    return;
                }

                callback.onMessage(JSON.parseObject(text, type));
            }

        });
    }

}
