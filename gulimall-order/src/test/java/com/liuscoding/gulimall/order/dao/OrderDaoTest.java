package com.liuscoding.gulimall.order.dao;
import java.math.BigDecimal;
import java.util.Date;

import com.liuscoding.gulimall.order.GulimallOrderApplicationTests;
import com.liuscoding.gulimall.order.entity.OrderEntity;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class OrderDaoTest extends GulimallOrderApplicationTests {

    @Autowired
    private OrderDao orderDao;

    @Test
    public void test(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setMemberId(11L);
        orderEntity.setOrderSn("订单号");
        orderEntity.setCouponId(110L);
        orderEntity.setCreateTime(new Date());
        orderEntity.setMemberUsername("刘帅");
        orderEntity.setTotalAmount(new BigDecimal("0"));
        orderEntity.setPayAmount(new BigDecimal("0"));
        orderEntity.setFreightAmount(new BigDecimal("0"));
        orderEntity.setPromotionAmount(new BigDecimal("0"));
        orderEntity.setIntegrationAmount(new BigDecimal("0"));
        orderEntity.setCouponAmount(new BigDecimal("0"));
        orderEntity.setDiscountAmount(new BigDecimal("0"));
        orderEntity.setPayType(0);
        orderEntity.setSourceType(0);
        orderEntity.setStatus(0);
        orderEntity.setDeliveryCompany("");
        orderEntity.setDeliverySn("");
        orderEntity.setAutoConfirmDay(0);
        orderEntity.setIntegration(0);
        orderEntity.setGrowth(0);
        orderEntity.setBillType(0);
        orderEntity.setBillHeader("");
        orderEntity.setBillContent("");
        orderEntity.setBillReceiverPhone("");
        orderEntity.setBillReceiverEmail("");
        orderEntity.setReceiverName("");
        orderEntity.setReceiverPhone("");
        orderEntity.setReceiverPostCode("");
        orderEntity.setReceiverProvince("");
        orderEntity.setReceiverCity("");
        orderEntity.setReceiverRegion("");
        orderEntity.setReceiverDetailAddress("");
        orderEntity.setNote("");
        orderEntity.setConfirmStatus(0);
        orderEntity.setDeleteStatus(0);
        orderEntity.setUseIntegration(0);
        orderEntity.setPaymentTime(new Date());
        orderEntity.setDeliveryTime(new Date());
        orderEntity.setReceiveTime(new Date());
        orderEntity.setCommentTime(new Date());
        orderEntity.setModifyTime(new Date());

        int insert = orderDao.insert(orderEntity);
        Assert.assertTrue(insert>0);


    }
}