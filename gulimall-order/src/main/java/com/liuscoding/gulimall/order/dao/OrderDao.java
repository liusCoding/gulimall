package com.liuscoding.gulimall.order.dao;

import com.liuscoding.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 13:45:51
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
