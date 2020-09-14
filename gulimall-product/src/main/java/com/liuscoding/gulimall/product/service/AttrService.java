package com.liuscoding.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.product.entity.AttrEntity;
import com.liuscoding.gulimall.product.vo.AttrGroupRelationVo;
import com.liuscoding.gulimall.product.vo.AttrRespVo;
import com.liuscoding.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:10:39
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存商品属性
     * @param attr 商品属性
     */
    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String type);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    /**
     * 查询没有分组的属性
     * @param params  分业参数
     * @param attrgroupId  属性分组id
     * @return 属性分页信息
     */
    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 查询已关联的属性列表
     * @param attrgroupId 属性分组id
     * @return  属性列表
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    /**
     * 删除属性
     * @param vos
     */
    void deleteRelation(List<AttrGroupRelationVo> vos);
}

