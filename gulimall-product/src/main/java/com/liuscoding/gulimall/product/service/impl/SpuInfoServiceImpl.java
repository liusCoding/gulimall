package com.liuscoding.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.liuscoding.common.constant.ProductConstant;
import com.liuscoding.common.to.SkuReductionTo;
import com.liuscoding.common.to.SpuBoundTo;
import com.liuscoding.common.to.es.SkuEsModel;
import com.liuscoding.common.to.es.SkuHasStockVo;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;
import com.liuscoding.common.utils.R;
import com.liuscoding.gulimall.product.dao.SpuInfoDao;
import com.liuscoding.gulimall.product.entity.*;
import com.liuscoding.gulimall.product.feign.CouponFeignService;
import com.liuscoding.gulimall.product.feign.SearchFeignService;
import com.liuscoding.gulimall.product.feign.WareFeignService;
import com.liuscoding.gulimall.product.service.*;
import com.liuscoding.gulimall.product.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {


    private final SpuInfoDescService spuInfoDescService;
    private final SpuImagesService spuImagesService;
    private final AttrService attrService;
    private final ProductAttrValueService productAttrValueService;
    private final CouponFeignService couponFeignService;
    private final SkuInfoService skuInfoService;
    private final SkuImagesService skuImagesService;
    private final SkuSaleAttrValueService skuSaleAttrValueService;
    private final WareFeignService wareFeignService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final SearchFeignService searchFeignService;

    public SpuInfoServiceImpl(SpuInfoDescService spuInfoDescService, SpuImagesService spuImagesService, AttrService attrService, ProductAttrValueService productAttrValueService, CouponFeignService couponFeignService, SkuInfoService skuInfoService, SkuImagesService skuImagesService, SkuSaleAttrValueService skuSaleAttrValueService, WareFeignService wareFeignService, BrandService brandService, CategoryService categoryService, SearchFeignService searchFeignService) {
        this.spuInfoDescService = spuInfoDescService;
        this.spuImagesService = spuImagesService;
        this.attrService = attrService;
        this.productAttrValueService = productAttrValueService;
        this.couponFeignService = couponFeignService;
        this.skuInfoService = skuInfoService;
        this.skuImagesService = skuImagesService;
        this.skuSaleAttrValueService = skuSaleAttrValueService;
        this.wareFeignService = wareFeignService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.searchFeignService = searchFeignService;
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
        BeanUtils.copyProperties(vo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.save(spuInfo);


        //2、保存spu的描述图片 pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);


        //3、保存spu的图片集pms_spu_image
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfo.getId(), images);


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
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfo.getId());
        R r = couponFeignService.saveSpuBounds(spuBoundTo);
        if (r.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        // 5、保存当前Spu对应的所有sku信息

        List<Skus> skus = vo.getSkus();
        if (CollectionUtils.isNotEmpty(images)) {
            skus.forEach(
                    item -> {
                        String defaultImg = "";
                        for (Images image : item.getImages()) {
                            if (image.getDefaultImg() == 1) {
                                defaultImg = image.getImgUrl();
                            }

                        }

                        SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                        BeanUtils.copyProperties(item, skuInfoEntity);
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
                        BeanUtils.copyProperties(item, skuReductionTo);

                        skuReductionTo.setSkuId(skuId);
                        if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(BigDecimal.ZERO) == 1) {
                            R result = couponFeignService.saveSkuReduction(skuReductionTo);
                            if (result.getCode() != 0) {
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
        queryWrapper.and(wq -> wq.eq(SpuInfoEntity::getId, key).or().like(SpuInfoEntity::getSpuName, key));
        String status = (String) params.get("status");
        queryWrapper.eq(StringUtils.isNotEmpty(status), SpuInfoEntity::getPublishStatus, status);
        String brandId = (String) params.get("brandId");
        queryWrapper.eq(StringUtils.isNotEmpty(brandId) && !brandId.equals("0"), SpuInfoEntity::getBrandId, brandId);
        String catelogId = (String) params.get("catelogId");
        queryWrapper.eq(StringUtils.isNotEmpty(catelogId) && !catelogId.equals("0"), SpuInfoEntity::getCatalogId, catelogId);

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), queryWrapper);


        return new PageUtils(page);
    }

    @Override
    public void up(Long spuId) {
        //1.查出当前spuId 对应的所有的sku信息，品牌的名字等
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.getSkusBySpuId(spuId);

        List<Long> skuIdList = skuInfoEntities.stream()
                .map(SkuInfoEntity::getSkuId)
                .collect(toList());

        //TODO 4.  查出当前sku的所有可以被用来检索的规格属性
        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.baseAttrlistforspu(spuId);

        //挑出是可以检索属性的信息，收集所有属性的id
        List<Long> attrIds = productAttrValueEntities.stream()
                .map(ProductAttrValueEntity::getAttrId)
                .collect(toList());


        //再根据上面收集到所有属性的id，去attr属性表里面挑出他们attr_id在我们指定的集合里面并且search_type是1.这个1是检索属性
        //过滤出一个都是检索属性的attrs的集合
        List<Long> searchIds = attrService.selectSearchAttrIds(attrIds);
        HashSet<Long> idSet = Sets.newHashSet(searchIds);

        List<SkuEsModel.Attrs> attrsList = productAttrValueEntities.stream()
                .filter(item -> idSet.contains(item.getAttrId()))
                .map(item -> {
                    //拿到检索的属性的元素
                    SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
                    BeanUtils.copyProperties(item, attrs);
                    return attrs;
                }).collect(toList());


        //TODO  发送远程调用，库存系统查询sku是否有库存
        Map<Long, Boolean> stockMap = null;
        //远程调用服务可能会发生异常
        try {
            R skusHasStock = wareFeignService.getSkusHasStock(skuIdList);
            TypeReference<List<SkuHasStockVo>> typeReference = new TypeReference<List<SkuHasStockVo>>() {
            };

            stockMap = skusHasStock.getData(typeReference).stream()
                    .collect(toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));

        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}", e);
        }

        //2.封装每个sku的信息
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> skuEsModels = skuInfoEntities.stream()
                .map(sku -> {
                    //组装数据到es
                    SkuEsModel skuEsModel = new SkuEsModel();
                    //组装数据到es
                    SkuEsModel esModel = new SkuEsModel();
                    BeanUtils.copyProperties(sku, esModel);
                    esModel.setSkuPrice(sku.getPrice());
                    esModel.setSkuImg(sku.getSkuDefaultImg());
                    //设置库存信息，到低有没有库存
                    if (finalStockMap == null) { //就算远程服务有问题，也让他有数据
                        esModel.setHasStock(true);
                    } else {
                        //才从map里面检索到的库存信息来
                        esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
                    }
                    //TODO 2、热度评分。0,
                    esModel.setHotScore(0L);


                    //TODO 3、查询品牌和分类的名字信息
                    BrandEntity brand = brandService.getById(esModel.getBrandId());
                    esModel.setBrandName(brand.getName());
                    esModel.setBrandImg(brand.getLogo());
                    CategoryEntity category = categoryService.getById(esModel.getCatalogId());
                    esModel.setCatalogName(category.getName());
                    esModel.setAttrs(attrsList); //设置检索属性
                    return esModel;
                }).collect(toList());

        //TODO 5、将数据发送给ES进行保存 gulimall-search
        R r = searchFeignService.productStatusUp(skuEsModels);
        if(r.getCode()==0){
            //TODO  6 .修改当前spu的状态
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else {  //远程调用失败
            //TODO 7、重复调用？接口幂等性; 重试机制？
        }

    }


}