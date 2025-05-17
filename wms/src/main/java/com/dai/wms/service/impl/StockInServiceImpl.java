package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.entity.StockIn;
import com.dai.wms.entity.StockInItem;
import com.dai.wms.mapper.StockInItemMapper;
import com.dai.wms.mapper.StockInMapper;
import com.dai.wms.service.StockInService;
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
 * @since 2025-04-14
 */
@Service
public class StockInServiceImpl extends ServiceImpl<StockInMapper, StockIn> implements StockInService {

    @Autowired
    private StockInMapper stockInMapper;
    @Autowired
    private StockInItemMapper stockInItemMapper;

    @Override
    public List<StockIn> list() {
        return stockInMapper.selectStockInList();
    }

    @Override
    public StockIn findByIdWithItems(Integer stockInId) {
        StockIn stockIn = stockInMapper.selectStockInById(stockInId);
        if (stockIn != null) {
            List<StockInItem> stockInItems = stockInItemMapper.selectStockInItemsByStockInId(stockInId);
            stockIn.setStockInItems(stockInItems);
        }
        return stockIn;
    }

    @Transactional
    @Override
    public boolean saveStockInWithItems(StockIn stockIn) {
        boolean stockInSaved = this.save(stockIn);

        if (stockInSaved) {
            Integer stockInId = stockIn.getStockInId();
            List<StockInItem> stockInItems = stockIn.getStockInItems();
            if (stockInItems != null && !stockInItems.isEmpty()) {
                for (StockInItem item : stockInItems) {
                    item.setStockInId(stockInId);
                    stockInItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateStockInWithItems(StockIn stockIn) {
        boolean stockInUpdated = this.updateById(stockIn);

        if (stockInUpdated) {
            Integer stockInId = stockIn.getStockInId();
            LambdaQueryWrapper<StockInItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(StockInItem::getStockInId, stockInId);
            stockInItemMapper.delete(queryWrapper);

            List<StockInItem> stockInItems = stockIn.getStockInItems();
            if (stockInItems != null && !stockInItems.isEmpty()) {
                for (StockInItem item : stockInItems) {
                    item.setStockInId(stockInId);
                    stockInItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public StockIn getStockInWithDetails(Integer stockInId) {
        return stockInMapper.selectStockInWithDetails(stockInId);  //   新增方法的实现
    }
}
