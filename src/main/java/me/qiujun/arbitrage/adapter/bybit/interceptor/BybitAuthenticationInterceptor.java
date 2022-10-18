package me.qiujun.arbitrage.adapter.bybit.interceptor;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.constant.BybitConstants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class BybitAuthenticationInterceptor implements Interceptor {

    private final List<String> WITHOUT_CONTENT_BODY_METHOD = Arrays.asList("GET", "DELETE", "HEAD");

    private final String apiKey;

    private final String secretKey;

    public BybitAuthenticationInterceptor(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder newRequestBuilder = original.newBuilder();

        if (original.header(BybitConstants.HEADER_SIGNATURE) != null) {
            String timestamp = String.valueOf(System.currentTimeMillis());

            String method = original.method();
            String requestParamsSignature;
            if (!WITHOUT_CONTENT_BODY_METHOD.contains(method)) {
                requestParamsSignature = getContentBody(original.body());
            } else {
                String query = original.url().encodedQuery();
                requestParamsSignature = query == null ? "" : query;
            }

            String signatureStr = timestamp + apiKey + BybitConstants.HEADER_RECEIVE_WINDOW_VALUE + requestParamsSignature;
            String signature = (new HmacUtils(HmacAlgorithms.HMAC_SHA_256, this.secretKey)).hmacHex(signatureStr);

            newRequestBuilder.removeHeader(BybitConstants.HEADER_SIGNATURE);
            newRequestBuilder.addHeader(BybitConstants.HEADER_TIMESTAMP, timestamp);
            newRequestBuilder.addHeader(BybitConstants.HEADER_API_KEY, this.apiKey);
            newRequestBuilder.addHeader(BybitConstants.HEADER_SIGNATURE, signature);
            newRequestBuilder.addHeader(BybitConstants.HEADER_SIGNATURE_TYPE, BybitConstants.HEADER_SIGNATURE_TYPE_VALUE);
            newRequestBuilder.addHeader(BybitConstants.HEADER_RECEIVE_WINDOW, BybitConstants.HEADER_RECEIVE_WINDOW_VALUE);
        }

        Request newRequest = newRequestBuilder.build();
        return chain.proceed(newRequest);
    }

    private String getContentBody(RequestBody body) {
        if (body == null) {
            return "";
        }

        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            log.error(null, e);
            return "";
        }
    }
}