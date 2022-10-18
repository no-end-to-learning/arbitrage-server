package me.qiujun.arbitrage.adapter.binance.interceptor;

import me.qiujun.arbitrage.adapter.binance.constant.BinanceApiConstants;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

public class BinanceAuthenticationInterceptor implements Interceptor {

    private final String apiKey;

    private final String secret;

    public BinanceAuthenticationInterceptor(String apiKey, String secret) {
        this.apiKey = apiKey;
        this.secret = secret;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder newRequestBuilder = original.newBuilder();

        boolean isApiKeyRequired = original.header(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY) != null;
        boolean isSignatureRequired = original.header(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED) != null;
        newRequestBuilder.removeHeader(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_APIKEY)
                .removeHeader(BinanceApiConstants.ENDPOINT_SECURITY_TYPE_SIGNED);

        // Endpoint requires sending a valid API-KEY
        if (isApiKeyRequired || isSignatureRequired) {
            newRequestBuilder.addHeader(BinanceApiConstants.API_KEY_HEADER, apiKey);
        }

        // Endpoint requires signing the payload
        if (isSignatureRequired) {
            HttpUrl url = original.url().newBuilder()
                    .addQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                    .build();

            String payload = url.query();
            if (!StringUtils.isEmpty(payload)) {
                HttpUrl signedUrl = url.newBuilder()
                        .addQueryParameter("signature", sign(payload, secret))
                        .build();
                newRequestBuilder.url(signedUrl);
            }
        }

        // Build new request after adding the necessary authentication information
        Request newRequest = newRequestBuilder.build();
        return chain.proceed(newRequest);
    }

    public static String sign(String message, String secret) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);
            return new String(Hex.encodeHex(sha256_HMAC.doFinal(message.getBytes())));
        } catch (Exception e) {
            throw new RuntimeException("Unable to sign message.", e);
        }
    }
}