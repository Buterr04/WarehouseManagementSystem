package com.dai.wms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dai.wms.entity.ReturnOrder;
import com.dai.wms.entity.ReturnOrderItem;
import com.dai.wms.mapper.ReturnOrderItemMapper;
import com.dai.wms.mapper.ReturnOrderMapper;
import com.dai.wms.service.ReturnOrderService;
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
public class ReturnOrderServiceImpl extends ServiceImpl<ReturnOrderMapper, ReturnOrder> implements ReturnOrderService {

    @Autowired
    private ReturnOrderMapper returnOrderMapper;
    @Autowired
    private ReturnOrderItemMapper returnOrderItemMapper;

    @Override
    public List<ReturnOrder> list() {
        return returnOrderMapper.selectReturnOrderList();
    }

    @Override
    public ReturnOrder findByIdWithItems(Integer returnId) {
        ReturnOrder returnOrder = returnOrderMapper.selectReturnOrderById(returnId);
        if (returnOrder != null) {
            List<ReturnOrderItem> returnOrderItems = returnOrderItemMapper.selectReturnOrderItemsByReturnId(returnId);
            returnOrder.setReturnOrderItems(returnOrderItems);
        }
        return returnOrder;
    }

    @Transactional
    @Override
    public boolean saveReturnOrderWithItems(ReturnOrder returnOrder) {
        boolean returnOrderSaved = this.save(returnOrder);

        if (returnOrderSaved) {
            Integer returnOrderId = returnOrder.getReturnId();
            List<ReturnOrderItem> returnOrderItems = returnOrder.getReturnOrderItems();
            if (returnOrderItems != null && !returnOrderItems.isEmpty()) {
                for (ReturnOrderItem item : returnOrderItems) {
                    item.setReturnId(returnOrderId);
                    returnOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public boolean updateReturnOrderWithItems(ReturnOrder returnOrder) {
        boolean returnOrderUpdated = this.updateById(returnOrder);

        if (returnOrderUpdated) {
            Integer returnOrderId = returnOrder.getReturnId();
            LambdaQueryWrapper<ReturnOrderItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReturnOrderItem::getReturnId, returnOrderId);
            returnOrderItemMapper.delete(queryWrapper);

            List<ReturnOrderItem> returnOrderItems = returnOrder.getReturnOrderItems();
            if (returnOrderItems != null && !returnOrderItems.isEmpty()) {
                for (ReturnOrderItem item : returnOrderItems) {
                    item.setReturnId(returnOrderId);
                    returnOrderItemMapper.insert(item);
                }
            }
            return true;
        }
        return false;
    }
}
