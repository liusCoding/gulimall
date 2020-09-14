package com.liuscoding.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.ware.dao.WareInfoDao;
import com.liuscoding.gulimall.ware.entity.WareInfoEntity;
import com.liuscoding.gulimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        LambdaQueryWrapper<WareInfoEntity> lambdaQuery = Wrappers.lambdaQuery();
        String key = (String) params.get("key");

        lambdaQuery.and(StringUtils.isNotEmpty(key),wq -> wq.eq(WareInfoEntity::getId,key).or().like(WareInfoEntity::getName,key)
                .or().like(WareInfoEntity::getAddress,key).or().like(WareInfoEntity::getAreacode,key));
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                lambdaQuery
        );

        return new PageUtils(page);
    }

}