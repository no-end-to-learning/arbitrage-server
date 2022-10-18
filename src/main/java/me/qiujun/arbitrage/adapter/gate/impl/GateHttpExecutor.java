package me.qiujun.arbitrage.adapter.gate.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.gate.bean.response.GateErrorResponse;
import me.qiujun.arbitrage.adapter.gate.exception.GateException;
import me.qiujun.arbitrage.adapter.gate.interceptor.GateAuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.exception.ExceptionUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

import java.io.IOException;

@Slf4j
public class GateHttpExecutor {

    public static <T> T createService(Class<T> serviceClass, OkHttpClient client, String httpBaseUrl, String apiKey, String secretKey) {
        GateAuthenticationInterceptor interceptor = new GateAuthenticationInterceptor(apiKey, secretKey);

        OkHttpClient okHttpClient = client.newBuilder()
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(httpBaseUrl)
                .client(okHttpClient)
                .build();

        return retrofit.create(serviceClass);
    }

    public static <T> T execute(Call<T> call) {
        Request request = call.request();
        try {
            long startAt = System.currentTimeMillis();
            Response<T> response = call.execute();
            long endAt = System.currentTimeMillis();

            log.debug("http request {} {} cost {}ms", request.method(), request.url(), endAt - startAt);

            if (response.isSuccessful()) {
                return response.body();
            }

            if (response.errorBody() != null) {
                throw new GateException(JSON.parseObject(response.errorBody().string(), GateErrorResponse.class));
            } else {
                throw new GateException("Unknown Exception");
            }
        } catch (IOException e) {
            log.error("request {} {} error {}", request.method(), request.url(), ExceptionUtils.getStackTrace(e));
            throw new GateException(e.getMessage());
        }
    }

}
