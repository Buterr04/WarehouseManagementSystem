package com.dai.wms.service;

import com.dai.wms.entity.StockInItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */
public interface StockInItemService extends IService<StockInItem> {

    List<StockInItem> getStockInItemsByStockInId(Integer stockInId);
    List<StockInItem> getStockInItemsWithProductInfo(Integer stockInId);  //   新增方法
}
