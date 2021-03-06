package com.liuscoding.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.order.entity.OrderSettingEntity;

import java.util.Map;

/**
 * 订单配置信息
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 13:45:51
 */
public interface OrderSettingService extends IService<OrderSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

