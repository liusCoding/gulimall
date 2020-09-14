package com.liuscoding.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 09:59:30
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

