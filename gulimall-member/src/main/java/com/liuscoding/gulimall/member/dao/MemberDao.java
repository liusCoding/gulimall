package com.liuscoding.gulimall.member.dao;

import com.liuscoding.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 09:59:30
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
