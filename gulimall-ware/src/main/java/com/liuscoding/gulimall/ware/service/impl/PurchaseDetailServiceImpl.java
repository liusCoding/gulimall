package com.liuscoding.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.ware.dao.PurchaseDetailDao;
import com.liuscoding.gulimall.ware.entity.PurchaseDetailEntity;
import com.liuscoding.gulimall.ware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<PurchaseDetailEntity> lambdaQuery = Wrappers.lambdaQuery();

        String key = (String) params.get("key");
        lambdaQuery.and(StringUtils.isNotEmpty(key),wq->wq.eq(PurchaseDetailEntity::getId,key)
                .or().eq(PurchaseDetailEntity::getSkuId,key));

        String status = (String) params.get("status");
        lambdaQuery.eq(StringUtils.isNotEmpty(status),PurchaseDetailEntity::getStatus,status);

        String wareId = (String) params.get("wareId");
        lambdaQuery.eq(StringUtils.isNotEmpty(wareId),PurchaseDetailEntity::getWareId,wareId);



        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                lambdaQuery
        );

        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {

        LambdaQueryWrapper<PurchaseDetailEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(PurchaseDetailEntity::getPurchaseId,id);
        return this.list(lambdaQuery);
    }

}