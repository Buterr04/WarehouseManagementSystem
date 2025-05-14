package com.dai.wms.service.impl;

import com.dai.wms.entity.StockOutItem;
import com.dai.wms.mapper.StockOutItemMapper;
import com.dai.wms.service.StockOutItemService;
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
 * @since 2025-04-22
 */
@Service
public class StockOutItemServiceImpl extends ServiceImpl<StockOutItemMapper, StockOutItem> implements StockOutItemService {

    @Autowired
    private StockOutItemMapper stockOutItemMapper;

    @Override
    public List<StockOutItem> getStockOutItemsWithProductInfo(Integer stockOutId) {
        return stockOutItemMapper.selectStockOutItemsWithProductInfo(stockOutId);
    }
}