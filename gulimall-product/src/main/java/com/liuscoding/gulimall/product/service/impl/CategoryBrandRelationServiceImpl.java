package com.liuscoding.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.liuscoding.gulimall.product.dao.BrandDao;
import com.liuscoding.gulimall.product.dao.CategoryDao;
import com.liuscoding.gulimall.product.entity.BrandEntity;
import com.liuscoding.gulimall.product.entity.CategoryEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.product.dao.CategoryBrandRelationDao;
import com.liuscoding.gulimall.product.entity.CategoryBrandRelationEntity;
import com.liuscoding.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    private final BrandDao brandDao;
    private final CategoryDao categoryDao;

    public CategoryBrandRelationServiceImpl(BrandDao brandDao, CategoryDao categoryDao) {
        this.brandDao = brandDao;
        this.categoryDao = categoryDao;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();

        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        BrandEntity brandEntity = brandDao.selectById(brandId);
        categoryBrandRelation.setBrandName(brandEntity.getName());
        this.save(categoryBrandRelation);

    }

    @Override
    public void updateBrand(Long brandId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CategoryBrandRelationEntity::getBrandId,brandId)
                .set(CategoryBrandRelationEntity::getBrandName,name);

        this.update(updateWrapper);
    }

    @Override
    public void updateCategory(Long catId, String name) {
        LambdaUpdateWrapper<CategoryBrandRelationEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CategoryBrandRelationEntity::getCatelogId,catId)
                .set(CategoryBrandRelationEntity::getCatelogName,name);
        this.update(updateWrapper);
    }

    @Override
    public List<BrandEntity> getBrandsByCateId(Long catId) {
        LambdaQueryWrapper<CategoryBrandRelationEntity> queryWrapper = new LambdaQueryWrapper<CategoryBrandRelationEntity>()
                .eq(CategoryBrandRelationEntity::getCatelogId, catId);

        List<CategoryBrandRelationEntity> relationEntities = this.list(queryWrapper);

        List<BrandEntity> brandEntities = relationEntities.stream().map(relation -> brandDao.selectById(relation.getBrandId())
        ).collect(Collectors.toList());
        return brandEntities;
    }

}