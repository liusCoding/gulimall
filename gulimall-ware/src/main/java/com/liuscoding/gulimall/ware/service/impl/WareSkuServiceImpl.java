package com.liuscoding.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;
import com.liuscoding.common.utils.R;
import com.liuscoding.gulimall.ware.dao.WareSkuDao;
import com.liuscoding.gulimall.ware.entity.WareSkuEntity;
import com.liuscoding.gulimall.ware.feign.ProductFeignService;
import com.liuscoding.gulimall.ware.service.WareSkuService;
import com.liuscoding.gulimall.ware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    ProductFeignService productFeignService;
    
    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        LambdaQueryWrapper<WareSkuEntity> lambdaQuery = Wrappers.lambdaQuery();

        String skuId = (String) params.get("skuId");
        lambdaQuery.eq(StringUtils.isNotEmpty(skuId),WareSkuEntity::getSkuId,skuId);

        String wareId = (String) params.get("wareId");
        lambdaQuery.eq(StringUtils.isNotEmpty(wareId),WareSkuEntity::getWareId,wareId);



        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                lambdaQuery
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有这个库存记录新增
        LambdaQueryWrapper<WareSkuEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(WareSkuEntity::getSkuId,skuId);
        lambdaQuery.eq(WareSkuEntity::getWareId,wareId);
        WareSkuEntity result = this.getOne(lambdaQuery);

        if(Objects.isNull(result)){
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setStockLocked(0);
            //TODO 远程查询sku的名字，如果失败，整个事务无需回滚
            //1.自己catch异常
            //TODO 还可以用什么办法让异常出现以后不回滚，高级

            R info = null;
            try {
                info = productFeignService.info(skuId);
                HashMap<String,Object> data = (HashMap<String, Object>) info.get("skuInfo");

                if(info.getCode() == 0){
                    wareSkuEntity.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            this.save(wareSkuEntity);

        }else {
            LambdaUpdateWrapper<WareSkuEntity> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(WareSkuEntity::getSkuId,skuId)
                    .eq(WareSkuEntity::getWareId,wareId)
                    .set(WareSkuEntity::getStock,skuNum + result.getStock());
            this.update(updateWrapper);
        }


    }

    @Override
    public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {
        //逐一检查
        List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
            SkuHasStockVo vo = new SkuHasStockVo();
            //查询当前sku的库存量
            //SELECT SUM(stock-stock_locked) FROM `wms_ware_sku` WHERE sku_id=1
            Long count = baseMapper.getSkuStock(skuId); //获取每一个sku的库存总量
            //按照这个计数count，就会判断是否有库存
            vo.setSkuId(skuId);
            vo.setHasStock(count==null?false:count>0); //有库存
            return vo;
        }).collect(Collectors.toList());

        return collect;
    }

}