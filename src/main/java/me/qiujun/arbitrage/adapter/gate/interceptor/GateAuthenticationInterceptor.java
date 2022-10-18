package me.qiujun.arbitrage.adapter.gate.interceptor;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.constant.GateConstants;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class GateAuthenticationInterceptor implements Interceptor {

    private final String apiKey;

    private final String secretKey;

    public GateAuthenticationInterceptor(String apiKey, String secretKey) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder newRequestBuilder = original.newBuilder();

        if (original.header(GateConstants.HEADER_SIGNATURE) != null) {
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);

            String method = original.method();
            String path = original.url().encodedPath();

            String query = original.url().encodedQuery();
            query = query == null ? "" : query;

            String bodySign = getContentSha512(original.body());

            String signatureStr = String.join("\n", Arrays.asList(method, path, query, bodySign, timestamp));
            String signature = (new HmacUtils(HmacAlgorithms.HMAC_SHA_512, this.secretKey)).hmacHex(signatureStr);

            newRequestBuilder.removeHeader(GateConstants.HEADER_SIGNATURE);
            newRequestBuilder.addHeader(GateConstants.HEADER_TIMESTAMP, timestamp);
            newRequestBuilder.addHeader(GateConstants.HEADER_API_KEY, this.apiKey);
            newRequestBuilder.addHeader(GateConstants.HEADER_SIGNATURE, signature);
        }

        Request newRequest = newRequestBuilder.build();
        return chain.proceed(newRequest);
    }

    private String getContentSha512(RequestBody body) {
        if (body == null) {
            return DigestUtils.sha512Hex("");
        }

        try {
            Buffer buffer = new Buffer();
            body.writeTo(buffer);
            return DigestUtils.sha512Hex(buffer.readUtf8());
        } catch (IOException e) {
            log.error(null, e);
            return "";
        }
    }

}