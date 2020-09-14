package com.liuscoding.gulimall.product.service.impl;

import com.google.common.collect.Lists;
import com.liuscoding.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.product.dao.CategoryDao;
import com.liuscoding.gulimall.product.entity.CategoryEntity;
import com.liuscoding.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {


    private final CategoryBrandRelationService categoryBrandRelationService;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {

        // 1.查出所有的分类
        List<CategoryEntity> categoryEntities = this.list();

        //2.组装成父子结构


        List<CategoryEntity> list = categoryEntities.stream()
                //找出所有的一级结构
                .filter(categoryEntity -> categoryEntity.getParentCid().equals(0L))
                .map(cate->{
                    cate.setChildren(getChildrens(cate,categoryEntities));
                    return cate;
                })
                .sorted(Comparator.comparingInt(cate -> (cate.getSort() == null ? 0 : cate.getSort())))
                .collect(Collectors.toList());
        return list;
    }

    @Override
    public void removeMenuByIds(List<Long> ids) {
        //1.检查当前删除的菜单，是否被别的地方引用

        //逻辑删除
        this.removeByIds(ids);
    }

    @Override
    public List<Long> fingCatelogPath(Long catelogId) {

        List<Long> paths = Lists.newArrayList();
        paths = findParentPath(catelogId, paths);
        //顺序反转
        Collections.reverse(paths);
        return paths;
    }

    /**
     * 级联更新分类
     * @param category 分类信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(),category.getName());

    }

    private List<Long> findParentPath(Long catelogId,List<Long> paths){
        //1.收集当前节点

        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if(categoryEntity.getParentCid() != 0){
            findParentPath(categoryEntity.getParentCid(),paths);
        }
        return paths;
    }

    /**
     *  递归查找所有分类的子分类
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(root.getCatId()))
                .map(cate -> {
                    //1.找到子菜单
                    cate.setChildren(getChildrens(cate, all));
                    return cate;
                }).sorted(Comparator.comparingInt(cate -> (cate.getSort() == null ? 0 : cate.getSort())))
                .collect(Collectors.toList());

        return children;
    }

}