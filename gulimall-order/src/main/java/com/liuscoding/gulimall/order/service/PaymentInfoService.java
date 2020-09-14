package com.liuscoding.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.order.entity.PaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 13:45:51
 */
public interface PaymentInfoService extends IService<PaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

