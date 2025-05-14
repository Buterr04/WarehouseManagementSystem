package com.dai.wms.service;

import com.dai.wms.entity.StockOutItem;
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
public interface StockOutItemService extends IService<StockOutItem> {

    List<StockOutItem> getStockOutItemsWithProductInfo(Integer stockOutId);
}