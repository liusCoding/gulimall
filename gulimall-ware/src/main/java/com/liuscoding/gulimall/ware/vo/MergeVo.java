package com.liuscoding.gulimall.ware.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;
}
