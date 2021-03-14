package com.liuscoding.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: GulimallElasticSearchConfig
 * @description: Es配置类
 * @author: liusCoding
 * @create: 2021-03-05 20:31
 */


@Configuration
public class GulimallElasticSearchConfig {
    /**
     * 1. 导入依赖
     * 2. 编写配置，给容器中注入一个RestHIghLevelClient
     * 3. 参照官方文档操作es
     */

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClient() {

        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost("120.79.185.188", 9200, "http"));

        return new RestHighLevelClient(clientBuilder);
    }


}
