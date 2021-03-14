package com.liuscoding.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.liuscoding.common.to.es.SkuEsModel;
import com.liuscoding.gulimall.search.config.GulimallElasticSearchConfig;
import com.liuscoding.gulimall.search.constant.EsConstant;
import com.liuscoding.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @className: ProductSaveServiceImpl
 * @description:
 * @author: liusCoding
 * @create: 2021-03-14 20:14
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private  RestHighLevelClient restHighLevelClient;



    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //数据保存到es中
        //1.给es中建立一个索引。product，建立映射关系

        //2.给es中保存数据，
        // BulkRequest bulkRequest, RequestOptions options
        BulkRequest bulkRequest = new BulkRequest();
        //构造批量操作
        for (SkuEsModel model : skuEsModels) {
            //构造保存的请求
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(model.getSkuId().toString());//当前商品的sku的id
            String s = JSON.toJSONString(model);
            indexRequest.source(s, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);//批量保存数据到es

        //TODO 1.如果批量错误，就可以处理错误
        boolean b = bulk.hasFailures();//统计哪些商品上架失败
        List<String> collect = Arrays.stream(bulk.getItems()).map(item -> {
            //拿到每一个的处理结果，进行处理
            return item.getId();
        }).collect(Collectors.toList());
        log.info("商品上架完成:{},返回数据: {}",collect,bulk.toString());

        return b;
    }
}
