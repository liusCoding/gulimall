package com.liuscoding.gulimall.ware.dao;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.Date;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuscoding.gulimall.ware.GulimallWareApplicationTests;
import com.liuscoding.gulimall.ware.entity.PurchaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
@Slf4j
public class PurchaseDaoTest extends GulimallWareApplicationTests {

    @Autowired
    private PurchaseDao purchaseDao;

    @Test
    public void save(){
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setAssigneeId(11L);
        purchaseEntity.setAssigneeName("刘帅");
        purchaseEntity.setPhone("144444");
        purchaseEntity.setPriority(0);
        purchaseEntity.setStatus(0);
        purchaseEntity.setWareId(0L);
        purchaseEntity.setAmount(new BigDecimal("1999"));
        purchaseEntity.setCreateTime(new Date());
        purchaseEntity.setUpdateTime(new Date());

        int result = purchaseDao.insert(purchaseEntity);
        Assert.assertTrue(result>0);

    }

    @Test
    public void query(){
        LambdaQueryWrapper<PurchaseEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PurchaseEntity::getAssigneeName,"刘帅");
        PurchaseEntity purchaseEntity = purchaseDao.selectOne(queryWrapper);
        log.info("【result】：{}",purchaseEntity);
        Assert.assertNotNull(purchaseEntity);
    }


}