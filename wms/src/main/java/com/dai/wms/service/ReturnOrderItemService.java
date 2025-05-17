package com.dai.wms.service;

import com.dai.wms.entity.ReturnOrderItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
public interface ReturnOrderItemService extends IService<ReturnOrderItem> {

    List<ReturnOrderItem> getReturnOrderItemsByReturnId(Integer returnId);
    List<ReturnOrderItem> getReturnOrderItemsWithProductInfo(Integer returnId);
}