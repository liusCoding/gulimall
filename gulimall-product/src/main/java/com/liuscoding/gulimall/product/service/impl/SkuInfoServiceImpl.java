package com.liuscoding.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;
import com.liuscoding.gulimall.product.dao.SkuInfoDao;
import com.liuscoding.gulimall.product.entity.SkuInfoEntity;
import com.liuscoding.gulimall.product.service.SkuInfoService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        LambdaQueryWrapper<SkuInfoEntity> lambdaQuery = Wrappers.lambdaQuery();
        String key = (String) params.get("key");
        lambdaQuery.and(StringUtils.isNotEmpty(key),wq -> wq.eq(SkuInfoEntity::getSkuId,key).or().like(SkuInfoEntity::getSkuName,key));

        String catelogId = (String) params.get("catelogId");
        lambdaQuery.eq(StringUtils.isNotEmpty(catelogId) && ! catelogId.equals("0"),SkuInfoEntity::getCatalogId,catelogId);

        String brandId = (String) params.get("brandId");
        lambdaQuery.eq(StringUtils.isNotEmpty(brandId) && !brandId.equals("0"),SkuInfoEntity::getBrandId,brandId );

        String min = (String) params.get("min");
        lambdaQuery.ge(StringUtils.isNotEmpty(min),SkuInfoEntity::getPrice,min);

        String max = (String) params.get("max");
        lambdaQuery.le(new BigDecimal(max).compareTo(BigDecimal.ZERO)>0,SkuInfoEntity::getPrice,max);

        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), lambdaQuery);
        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        LambdaQueryWrapper<SkuInfoEntity> skuWrapper = Wrappers.lambdaQuery();
        return this.list(skuWrapper.eq(SkuInfoEntity::getSpuId, spuId));

    }

}