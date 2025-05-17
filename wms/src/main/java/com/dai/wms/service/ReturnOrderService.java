package com.dai.wms.service;

import com.dai.wms.entity.ReturnOrder;
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
public interface ReturnOrderService extends IService<ReturnOrder> {

    boolean saveReturnOrderWithItems(ReturnOrder returnOrder);
    boolean updateReturnOrderWithItems(ReturnOrder returnOrder);
    List<ReturnOrder> list();
    ReturnOrder findByIdWithItems(Integer returnId);
}
