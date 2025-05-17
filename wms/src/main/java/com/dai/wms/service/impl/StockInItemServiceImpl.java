package com.dai.wms.service.impl;

import com.dai.wms.entity.StockInItem;
import com.dai.wms.mapper.StockInItemMapper;
import com.dai.wms.service.StockInItemService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */
@Service
public class StockInItemServiceImpl extends ServiceImpl<StockInItemMapper, StockInItem> implements StockInItemService {

    @Autowired
    private StockInItemMapper stockInItemMapper;

    @Override
    public List<StockInItem> getStockInItemsByStockInId(Integer stockInId) {
        return stockInItemMapper.selectStockInItemsByStockInId(stockInId);
    }

    @Override
    public List<StockInItem> getStockInItemsWithProductInfo(Integer stockInId) {
        return stockInItemMapper.selectStockInItemsWithProductInfo(stockInId);  //   新增方法的实现
    }
}
