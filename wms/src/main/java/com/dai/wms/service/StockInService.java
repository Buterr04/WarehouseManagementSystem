package com.dai.wms.service;

import com.dai.wms.entity.StockIn;
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
public interface StockInService extends IService<StockIn> {

    boolean saveStockInWithItems(StockIn stockIn);
    boolean updateStockInWithItems(StockIn stockIn);
    List<StockIn> list();
    StockIn findByIdWithItems(Integer stockInId);
    StockIn getStockInWithDetails(Integer stockInId);  //   新增方法
}
