package com.liuscoding.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.liuscoding.gulimall.product.vo.AttrGroupRelationVo;

import java.util.List;
import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-11 16:10:39
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 保存属性和属性关联关系
     * @param vos 关联关系Vo
     */
    void saveBatch(List<AttrGroupRelationVo> vos);
}

