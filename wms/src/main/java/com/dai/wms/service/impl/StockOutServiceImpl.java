package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.entity.StockOut;
import com.dai.wms.entity.StockOutItem;
import com.dai.wms.mapper.StockOutMapper;
import com.dai.wms.mapper.StockOutItemMapper;
import com.dai.wms.service.StockOutService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class StockOutServiceImpl extends ServiceImpl<StockOutMapper, StockOut> implements StockOutService {

    @Autowired
    private StockOutMapper stockOutMapper;
    @Autowired
    private StockOutItemMapper stockOutItemMapper;

    @Override
    public List<StockOut> list() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<StockOut> getStockOutListWithSaleDate() {
        return stockOutMapper.selectStockOutListWithSaleDate();
    }

    @Override
    public StockOut findByIdWithSaleDate(Integer stockOutId) {
        return stockOutMapper.selectStockOutByIdWithSaleDate(stockOutId);
    }


    @Transactional
    @Override
    public boolean saveStockOutWithItems(StockOut stockOut) {
        boolean stockOutSaved = this.save(stockOut);

        if (stockOutSaved) {
            Integer stockOutId = stockOut.getStockOutId();
            List<StockOutItem> stockOutItems = stockOut.getStockOutItems();
            if (stockOutItems != null && !stockOutItems.isEmpty()) {
                for (StockOutItem item : stockOutItems) {
                    item.setStockOutId(stockOutId);
                    stockOutItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateStockOutWithItems(StockOut stockOut) {
        // 1. 更新 StockOut 本身
        boolean stockOutUpdated = this.updateById(stockOut);

        if (stockOutUpdated) {
            Integer stockOutId = stockOut.getStockOutId();

            // 2. 删除原有的 StockOutItem
            LambdaQueryWrapper<StockOutItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StockOutItem::getStockOutId, stockOutId);
            stockOutItemMapper.delete(queryWrapper);

            // 3. 插入新的 StockOutItem
            List<StockOutItem> stockOutItems = stockOut.getStockOutItems();
            if (stockOutItems != null && !stockOutItems.isEmpty()) {
                for (StockOutItem item : stockOutItems) {
                    item.setStockOutId(stockOutId);
                    stockOutItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean updateStatusById(Integer stockOutId, String status) {
        stockOutMapper.updateStatusById(stockOutId, status);
        return true;
    }

}