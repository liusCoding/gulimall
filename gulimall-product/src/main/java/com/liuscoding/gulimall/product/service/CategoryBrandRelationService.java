package com.liuscoding.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.product.entity.BrandEntity;
import com.liuscoding.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:10:37
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    /**
     *  根据品牌id修改品牌名称
     * @param brandId 品牌id
     * @param name  名称
     */
    void updateBrand(Long brandId, String name);

    /**
     *  根据分类id修改分类名称
     * @param catId 分类id
     * @param name  分类名称
     */
    void updateCategory(Long catId, String name);

    /**
     * 根据分类id查询品牌列表
     * @param catId 分类
     * @return 品牌列表
     */
    List<BrandEntity> getBrandsByCateId(Long catId);
}

