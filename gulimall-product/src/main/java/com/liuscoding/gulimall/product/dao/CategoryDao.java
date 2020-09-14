package com.liuscoding.gulimall.product.dao;

import com.liuscoding.gulimall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:10:38
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
