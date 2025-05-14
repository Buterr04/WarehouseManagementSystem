package com.dai.wms.service;

import com.dai.wms.entity.StockOut;
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
public interface StockOutService extends IService<StockOut> {
    boolean saveStockOutWithItems(StockOut stockOut);
    boolean updateStockOutWithItems(StockOut stockOut);
    List<StockOut> list();
    List<StockOut> getStockOutListWithSaleDate();
    StockOut findByIdWithSaleDate(Integer stockOutId);
}
