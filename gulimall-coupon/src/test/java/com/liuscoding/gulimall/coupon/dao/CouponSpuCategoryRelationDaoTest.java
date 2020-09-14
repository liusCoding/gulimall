package com.liuscoding.gulimall.coupon.dao;


import com.liuscoding.gulimall.coupon.GulimallCouponApplicationTests;
import com.liuscoding.gulimall.coupon.entity.CouponSpuCategoryRelationEntity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class CouponSpuCategoryRelationDaoTest extends GulimallCouponApplicationTests {

    @Autowired
    private CouponSpuCategoryRelationDao couponSpuCategoryRelationDao;
    @Test
    public void save(){

        CouponSpuCategoryRelationEntity relationEntity = new CouponSpuCategoryRelationEntity();;
        relationEntity.setCouponId(1L);
        relationEntity.setCategoryId(2L);
        relationEntity.setCategoryName("家用电器");

        int insert = couponSpuCategoryRelationDao.insert(relationEntity);
        Assert.assertTrue(insert>0);

    }
}