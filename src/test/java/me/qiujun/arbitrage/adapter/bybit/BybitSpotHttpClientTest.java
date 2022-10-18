package me.qiujun.arbitrage.adapter.bybit;

import lombok.extern.slf4j.Slf4j;
import me.qiujun.arbitrage.Starter;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitOrderCancelByIdsResponse;
import me.qiujun.arbitrage.adapter.bybit.bean.response.BybitOrderResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Starter.class)
public class BybitSpotHttpClientTest {

    @Autowired
    private BybitSpotHttpClient bybitSpotHttpClient;

    @Test
    public void testGetOrder() {
        BybitOrderResponse order = bybitSpotHttpClient.getOrder("1254544213733850368");
        System.out.println(order);
    }

    @Test
    public void testCancelOrders() {
        List<String> orderIds = Collections.singletonList("1250076288356552960");
        List<BybitOrderCancelByIdsResponse.Item> result = bybitSpotHttpClient.cancelOrder(orderIds);
        System.out.println(result);
    }

}
