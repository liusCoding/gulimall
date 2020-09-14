package com.liuscoding.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:10:38
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     *  查询所有的分类
     * @return  List<CategoryEntity>
     */
    List<CategoryEntity> listWithTree();

    /**
     * 删除分类
     * @param ids
     */
    void removeMenuByIds(List<Long> ids);

    /**
     * 查找分类路径
     */
    public List<Long> fingCatelogPath(Long catelogId);

    /**
     * 级联更新
     * @param category
     */
    void updateCascade(CategoryEntity category);
}

