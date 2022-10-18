package me.qiujun.arbitrage.adapter.binance.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.adapter.binance.bean.response.BinanceErrorResponse;
import me.qiujun.arbitrage.adapter.binance.exception.BinanceException;
import me.qiujun.arbitrage.adapter.binance.interceptor.BinanceAuthenticationInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.apache.commons.lang.exception.ExceptionUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.fastjson.FastJsonConverterFactory;

import java.io.IOException;

@Slf4j
public class BinanceExecutor {

    public static <T> T createService(Class<T> serviceClass, OkHttpClient client, String httpBaseUrl, String apiKey, String secret) {
        BinanceAuthenticationInterceptor interceptor = new BinanceAuthenticationInterceptor(apiKey, secret);

        OkHttpClient adaptedClient = client.newBuilder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(FastJsonConverterFactory.create())
                .baseUrl(httpBaseUrl)
                .client(adaptedClient)
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
                throw new BinanceException(JSON.parseObject(response.errorBody().string(), BinanceErrorResponse.class));
            } else {
                throw new BinanceException("Unknown Exception");
            }
        } catch (IOException e) {
            log.error("request {} {} error {}", request.method(), request.url(), ExceptionUtils.getStackTrace(e));
            throw new BinanceException(e.getMessage());
        }
    }

}
