package com.liuscoding.gulimall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.liuscoding.common.constant.WareConstant;
import com.liuscoding.common.exception.BizCodeEnume;
import com.liuscoding.common.exception.GuliException;
import com.liuscoding.gulimall.ware.entity.PurchaseDetailEntity;
import com.liuscoding.gulimall.ware.service.PurchaseDetailService;
import com.liuscoding.gulimall.ware.service.WareSkuService;
import com.liuscoding.gulimall.ware.vo.MergeVo;
import com.liuscoding.gulimall.ware.vo.PurchaseDoneVo;
import com.liuscoding.gulimall.ware.vo.PurchaseItemDoneVo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuscoding.common.utils.PageUtils;
import com.liuscoding.common.utils.Query;

import com.liuscoding.gulimall.ware.dao.PurchaseDao;
import com.liuscoding.gulimall.ware.entity.PurchaseEntity;
import com.liuscoding.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    private final PurchaseDetailService purchaseDetailService;

    private final WareSkuService wareSkuService;

    public PurchaseServiceImpl(PurchaseDetailService purchaseDetailService, WareSkuService wareSkuService) {
        this.purchaseDetailService = purchaseDetailService;
        this.wareSkuService = wareSkuService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceivePurchase(Map<String, Object> params) {

        LambdaQueryWrapper<PurchaseEntity> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(PurchaseEntity::getStatus, WareConstant.PurchaseStatusEnum.CREATED)
                .or().eq(PurchaseEntity::getStatus,WareConstant.PurchaseStatusEnum.ASSIGNED);

        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), lambdaQuery);
        return new PageUtils(page);
    }

    /**
     * 合并采购单
     * @param mergeVo
     */
    @Override
    public void mergePurchase(MergeVo mergeVo) {
        List<Long> items = mergeVo.getItems();
        Long purchaseId = mergeVo.getPurchaseId();
        if(Objects.isNull(purchaseId)){
            //1.新建一个采购单
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());

            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }

        //TODO 确认采购单状态是 0,1才可以合并
        PurchaseEntity purchaseResult = this.getById(purchaseId);
        if (purchaseResult.getStatus()==0 || purchaseResult.getStatus() == 1){
            throw  new GuliException(BizCodeEnume.OPERATION_ERROR);
        }

        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> details = items.stream().map(item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(item);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.ASSIGNED.getCode());
            return purchaseDetailEntity;

        }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(details);
        PurchaseEntity result = new PurchaseEntity();
        result.setId(finalPurchaseId);
        result.setUpdateTime(new Date());
        this.updateById(result);

    }

    /**
     * 采购单
     * @param ids
     */
    @Override
    public void received(List<Long> ids) {
        //1.确认当前采购单是新建或者已分配状态

        List<PurchaseEntity> purchaseEntities = ids.stream().map(id -> {
            PurchaseEntity purchaseEntity = this.getById(id);
            return purchaseEntity;
        }).filter(item -> {
            if (item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode() ||
                    item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode()
            ) {
                return true;
            }
            return false;
        }).map(item -> {
            item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
            item.setUpdateTime(new Date());
            return item;
        }).collect(Collectors.toList());

        //2、改变采购单的状态
        this.updateBatchById(purchaseEntities);

        //3、改变采购项的状态
        purchaseEntities.forEach(item ->{
            List<PurchaseDetailEntity> list = purchaseDetailService.listDetailByPurchaseId(item.getId());
            List<PurchaseDetailEntity> details = list.stream().map(entity -> {
                PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
                purchaseDetailEntity.setId(entity.getId());
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());

                return purchaseDetailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(details);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void done(PurchaseDoneVo doneVo) {
        Long id = doneVo.getId();

        //2、改变采购项的状态
        AtomicReference<Boolean> flag = new AtomicReference<>(true);

        List<PurchaseItemDoneVo> items = doneVo.getItems();
        List<PurchaseDetailEntity> updates = new ArrayList<PurchaseDetailEntity>();

        items.stream().forEach( item -> {
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            if ( item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()){
                flag.set(true);
                purchaseDetailEntity.setStatus(item.getStatus());
            }else {
                purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3.将成功采购的进行入库
                PurchaseDetailEntity detailEntity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(detailEntity.getSkuId(),detailEntity.getWareId(),detailEntity.getSkuNum());
            }
        });
    }

}