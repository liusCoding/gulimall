package com.liuscoding.gulimall.member.dao;
import java.util.Date;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.liuscoding.gulimall.member.GulimallMemberApplicationTests;
import com.liuscoding.gulimall.member.entity.MemberLoginLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class MemberLoginLogDaoTest extends GulimallMemberApplicationTests {

    @Autowired
    private MemberLoginLogDao memberLoginLogDao;

    @Test
    public void insert(){
        MemberLoginLogEntity memberLoginLogEntity = new MemberLoginLogEntity();
        memberLoginLogEntity.setMemberId(10L);
        memberLoginLogEntity.setCreateTime(new Date());
        memberLoginLogEntity.setIp("188.188.188.19");
        memberLoginLogEntity.setCity("深圳");
        memberLoginLogEntity.setLoginType(0);

        int insert = memberLoginLogDao.insert(memberLoginLogEntity);
        Assert.assertTrue(insert>0);
    }


    @Test
    public  void query(){
        LambdaQueryWrapper<MemberLoginLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberLoginLogEntity::getMemberId,10L);
        MemberLoginLogEntity memberLoginLogEntity = memberLoginLogDao.selectOne(queryWrapper);
        log.info("【result】:{}",memberLoginLogEntity);
        Assert.assertNotNull(memberLoginLogEntity);
    }
}