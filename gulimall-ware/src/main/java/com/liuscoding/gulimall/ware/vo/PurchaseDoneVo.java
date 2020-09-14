package com.liuscoding.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liuscoding
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id ;

    private List<PurchaseItemDoneVo> items;
}
