package com.liuscoding.gulimall.coupon.dao;

import com.liuscoding.gulimall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 09:31:37
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
