package com.liuscoding.gulimall.member.feign;

import com.liuscoding.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author liuscoding
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService{

    @RequestMapping("/coupon/coupon/member/list")
    R memberCoupons();
}
