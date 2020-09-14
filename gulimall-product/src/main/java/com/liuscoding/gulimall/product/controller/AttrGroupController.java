package com.liuscoding.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.liuscoding.gulimall.product.entity.AttrEntity;
import com.liuscoding.gulimall.product.service.AttrAttrgroupRelationService;
import com.liuscoding.gulimall.product.service.AttrService;
import com.liuscoding.gulimall.product.service.CategoryService;
import com.liuscoding.gulimall.product.vo.AttrGroupRelationVo;
import com.liuscoding.gulimall.product.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liuscoding.gulimall.product.entity.AttrGroupEntity;
import com.liuscoding.gulimall.product.service.AttrGroupService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.R;



/**
 * 属性分组
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:45:17
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService relationService;


    @PostMapping("/attr/relation")
    public R addRelation(@RequestBody List<AttrGroupRelationVo> vos){
        relationService.saveBatch(vos);
        return R.ok();
    }

    @GetMapping("/{catelogId}/withattr")
    public R getAttrGroupWithAttrs(@PathVariable("catelogId") Long catalogId){
        //1.   查出当前分类下的所有属性分组

        //2.   查出每个属性分组的所有属性

       List<AttrGroupWithAttrsVo> vos =  attrGroupService.getAttrGroupWithAttrsByCatelogId(catalogId);
       return R.ok().put("data",vos);
    }

    @GetMapping("/{attrgroupId}/attr/relation")
    public R attrRelation(@PathVariable("attrgroupId") Long attrgroupId){
        List<AttrEntity> entities = attrService.getRelationAttr(attrgroupId);
        return R.ok().put("data",entities);
    }
    /**
     * 查询未分组的属性
     * @param attrgroupId 属性分组id
     * @param params 分页参数
     * @return 分业信息
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public R attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId,@RequestParam Map<String,Object> params){
       PageUtils page =  attrService.getNoRelationAttr(params,attrgroupId);
       return R.ok().put("page",page);
    }


    @PostMapping("/attr/relation/delete")
    public R deleteRelation(@RequestBody List<AttrGroupRelationVo> vos){
        attrService.deleteRelation(vos);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,@PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrGroupService.queryPage(params);
          PageUtils page = attrGroupService.queryPage(params,catelogId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long categoryId = attrGroup.getCatelogId();

        List<Long> categoryPaths = categoryService.fingCatelogPath(categoryId);
        attrGroup.setCatelogPath(categoryPaths);
        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
