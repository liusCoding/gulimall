package com.liuscoding.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 09:59:30
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

