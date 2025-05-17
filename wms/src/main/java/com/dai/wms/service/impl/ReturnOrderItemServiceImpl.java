package com.dai.wms.service.impl;

import com.dai.wms.entity.ReturnOrderItem;
import com.dai.wms.mapper.ReturnOrderItemMapper;
import com.dai.wms.service.ReturnOrderItemService;
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
public class ReturnOrderItemServiceImpl extends ServiceImpl<ReturnOrderItemMapper, ReturnOrderItem> implements ReturnOrderItemService {

    @Autowired
    private ReturnOrderItemMapper returnOrderItemMapper;

    @Override
    public List<ReturnOrderItem> getReturnOrderItemsByReturnId(Integer returnId) {
        return returnOrderItemMapper.selectReturnOrderItemsByReturnId(returnId);
    }

    @Override
    public List<ReturnOrderItem> getReturnOrderItemsWithProductInfo(Integer returnId) {
        return returnOrderItemMapper.selectReturnOrderItemsWithProductInfo(returnId);
    }
}
