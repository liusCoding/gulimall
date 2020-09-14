package com.liuscoding.gulimall.product.service;

import com.alibaba.fastjson.JSON;
import com.liuscoding.gulimall.product.GulimallProductApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

@Slf4j
public class CategoryServiceTest extends GulimallProductApplicationTests {

    @Autowired
    CategoryService categoryService;
    @Test
    public void fingCatelogPath() {
        List<Long> catelogPath = categoryService.fingCatelogPath(993L);
        log.info("categoryInfo:{}", JSON.toJSONString(catelogPath,true));
    }
}