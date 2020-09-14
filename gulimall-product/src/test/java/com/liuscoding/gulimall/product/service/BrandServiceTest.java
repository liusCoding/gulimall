package com.liuscoding.gulimall.product.service;



import com.liuscoding.gulimall.product.GulimallProductApplicationTests;
import com.liuscoding.gulimall.product.entity.BrandEntity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BrandServiceTest extends GulimallProductApplicationTests {

    @Autowired
    private BrandService brandService;

    @Test
    public void add(){
        BrandEntity brandEntity = new BrandEntity();

        brandEntity.setName("华为");
        brandEntity.setLogo("huawei");
        brandEntity.setDescript("不仅仅是五百强");
        brandEntity.setShowStatus(0);
        brandEntity.setFirstLetter("H");
        brandEntity.setSort(0);

        boolean save = brandService.save(brandEntity);
        Assert.assertTrue(save);
    }
}