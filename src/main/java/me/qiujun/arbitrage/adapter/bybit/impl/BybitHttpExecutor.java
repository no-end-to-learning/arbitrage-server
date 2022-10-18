package me.qiujun.arbitrage.adapter.bybit.impl;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitBaseResponse;
import me.qiujun.arbitrage.adapter.bybit.exception.BybitException;
import me.qiujun.arbitrage.adapter.bybit.interceptor.BybitAuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.exception.ExceptionUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

import java.io.IOException;

@Slf4j
public class BybitHttpExecutor {

    public static <T> T createService(Class<T> serviceClass, OkHttpClient client, String httpBaseUrl, String apiKey, String secretKey) {
        BybitAuthenticationInterceptor interceptor = new BybitAuthenticationInterceptor(apiKey, secretKey);

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

    public static <T extends BybitBaseResponse<?>> T execute(Call<T> call) {
        Request request = call.request();

        try {
            long startAt = System.currentTimeMillis();
            Response<T> response = call.execute();
            long endAt = System.currentTimeMillis();

            log.debug("http request {} {} cost {}ms", request.method(), request.url(), endAt - startAt);

            if (response.isSuccessful() && response.body() != null) {
                if (response.body().getRetCode() == 0) {
                    return response.body();
                } else {
                    throw new BybitException(response.body());
                }
            }

            log.error("{} {} ", response, response.body());

            throw new BybitException("Unknown Exception");
        } catch (IOException e) {
            log.error("request {} {} error {}", request.method(), request.url(), ExceptionUtils.getStackTrace(e));
            throw new BybitException(e.getMessage());
        }
    }

}
