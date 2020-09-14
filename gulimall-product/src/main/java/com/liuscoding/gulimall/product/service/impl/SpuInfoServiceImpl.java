package com.liuscoding.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuscoding.common.to.SkuReductionTo;
import com.liuscoding.common.to.SpuBoundTo;
import com.liuscoding.common.utils.R;
import com.liuscoding.gulimall.product.entity.*;
import com.liuscoding.gulimall.product.feign.CouponFeignService;
import com.liuscoding.gulimall.product.service.*;
import com.liuscoding.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    private final  SpuInfoDescService spuInfoDescService;
    private final SpuImagesService spuImagesService;
    private final AttrService attrService;
    private final ProductAttrValueService productAttrValueService;
    private final CouponFeignService couponFeignService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;

    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService, SpuImagesService spuImagesService, AttrService attrService, ProductAttrValueService productAttrValueService, CouponFeignService couponFeignService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.productAttrValueService = productAttrValueService;
        this.couponFeignService = couponFeignService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuInfo(SpuSaveVo vo) {
        //1、保存spu基本信息 pms_spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo,spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.save(spuInfo);


        //2、保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",",decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);


        //3、保存spu的图片集pms_spu_image
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfo.getId(),images);


        //4.保存spu的规格参数 pms_product_attr_values
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrList = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity attrValue = new ProductAttrValueEntity();
            attrValue.setAttrId(attr.getAttrId());
            AttrEntity attrEntity = attrService.getById(attr.getAttrId());
            attrValue.setQuickShow(attr.getShowDesc());
            attrValue.setAttrName(attrEntity.getAttrName());
            attrValue.setSpuId(spuInfo.getId());

            return attrValue;
        }).collect(Collectors.toList());

        productAttrValueService.saveProductAttr(productAttrList);


        //4、保存spu的积分信息： gulimall_sms -> sms_spu+bounds
        Bounds bounds = vo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds,spuBoundTo);
        spuBoundTo.setSpuId(spuInfo.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if(r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        // 5、保存当前Spu对应的所有sku信息

        List<Skus> skus = vo.getSkus();
        if (CollectionUtils.isNotEmpty(images)) {
            skus.forEach(
                    item ->{
                        String defaultImg = "";
                        for (Images image : item.getImages()) {
                            if (image.getDefaultImg() == 1){
                                defaultImg = image.getImgUrl();
                            }

                        }

                        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                        BeanUtils.copyProperties(item,skuInfoEntity);
                        skuInfoEntity.setBrandId(spuInfo.getBrandId());
                        skuInfoEntity.setCatalogId(spuInfo.getCatalogId());
                        skuInfoEntity.setSaleCount(0L);
                        skuInfoEntity.setSpuId(spuInfo.getId());
                        skuInfoEntity.setSkuDefaultImg(defaultImg);

                        //5.1 ) sku的基本信息 pms_sku_info

                        skuInfoService.save(skuInfoEntity);

                        Long skuId = skuInfoEntity.getSkuId();

                        List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(
                                img -> {
                                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                                    skuImagesEntity.setSkuId(skuId);
                                    skuImagesEntity.setImgUrl(img.getImgUrl());
                                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                                    return skuImagesEntity;
                                }
                        ).filter(img -> StringUtils.isNotEmpty(img.getImgUrl()))
                                .collect(Collectors.toList());

                        //5.2 )  sku的图片信息 pms_sku_iamge
                        skuImagesService.saveBatch(imagesEntities);


                        List<Attr> itemAttr = item.getAttr();
                        List<SkuSaleAttrValueEntity> saleAttrValueEntities = itemAttr.stream().map(attr -> {
                            SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                            BeanUtils.copyProperties(attr, attrValueEntity);
                            attrValueEntity.setSkuId(skuId);
                            return attrValueEntity;
                        }).collect(Collectors.toList());

                        //5.3 ) sku的销售属性信息： pms_sku_sale_attr_value
                        skuSaleAttrValueService.saveBatch(saleAttrValueEntities);


                        //5.4 ) sku的优惠、满减等信息 gulimall_sms->sms_sku_ladder\sms_sku_full_reduction\sms_member_price
                        SkuReductionTo skuReductionTo = new SkuReductionTo();
                        BeanUtils.copyProperties(item,skuReductionTo);

                        skuReductionTo.setSkuId(skuId);
                        if (skuReductionTo .getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1){
                            R result = couponFeignService.saveSkuReduction(skuReductionTo);
                            if( result.getCode() != 0){
                                log.error("远程保存sku优惠信息失败");
                            }
                        }

                    }
            );
        }


    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        LambdaQueryWrapper<SpuInfoEntity> queryWrapper = Wrappers.lambdaQuery();

        String key = (String) params.get("key");
        queryWrapper.and(wq -> wq.eq(SpuInfoEntity::getId,key).or().like(SpuInfoEntity::getSpuName,key));
        String status = (String) params.get("status");
        queryWrapper.eq(StringUtils.isNotEmpty(status),SpuInfoEntity::getPublishStatus,status);
        String brandId = (String) params.get("brandId");
        queryWrapper.eq(StringUtils.isNotEmpty(brandId)&& !brandId.equals("0"),SpuInfoEntity::getBrandId,brandId);
        String catelogId = (String) params.get("catelogId");
        queryWrapper.eq(StringUtils.isNotEmpty(catelogId) && !catelogId.equals("0"),SpuInfoEntity::getCatalogId,catelogId);

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);


        return new PageUtils(page);
    }

}