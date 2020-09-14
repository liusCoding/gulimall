package com.liuscoding.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


import com.liuscoding.gulimall.ware.vo.MergeVo;
import com.liuscoding.gulimall.ware.vo.PurchaseDoneVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.liuscoding.gulimall.ware.entity.PurchaseEntity;
import com.liuscoding.gulimall.ware.service.PurchaseService;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.R;



/**
 * 采购信息
 *
 * @author liuscoding
 * @email liuscoding@163.com
 * @date 2020-08-12 10:51:44
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("/done")
    public R finish(@RequestBody PurchaseDoneVo doneVo){
        purchaseService.done(doneVo);
        return R.ok();
    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/unreceive/list")
    public R unreceiveList(@RequestParam Map<String,Object> params){
       PageUtils page =  purchaseService.queryPageUnreceivePurchase(params);
       return R.ok().put("page",page);
    }


    @PostMapping("/received")
    public  R received(@RequestBody List<Long> ids){
        purchaseService.received(ids);
        return R.ok();
    }
    /**
     *
     * @param mergeVo 合并信息
     * @return
     */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.mergePurchase(mergeVo);
        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase){
		purchaseService.save(purchase);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
