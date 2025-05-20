package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.StockIn;

import com.dai.wms.entity.StockInItem;
import com.dai.wms.service.StockInService;
import com.dai.wms.service.StockInItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-04-14
 */
@RestController
@RequestMapping("/stock-in")
public class StockInController {

    @Autowired
    private StockInService stockInService;
    @Autowired
    private StockInItemService stockInItemService;

    @GetMapping("/list")
    public Result list() {
        List<StockIn> stockInList = stockInService.list();
        return Result.success(stockInList);
    }

    @PostMapping("/save")
    public Result save(@RequestBody StockIn stockIn) {
        boolean stockInSaved = stockInService.saveStockInWithItems(stockIn);

        if (stockInSaved) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @PostMapping("/modify")
    public Result modify(@RequestBody StockIn stockIn) {
        boolean stockInUpdated = stockInService.updateStockInWithItems(stockIn);

        if (stockInUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/{stockInId}")
    public Result getStockIn(@PathVariable Integer stockInId) {
        StockIn stockIn = stockInService.findByIdWithItems(stockInId);
        if (stockIn != null) {
            return Result.success(stockIn);
        } else {
            return Result.fail();
        }
    }

    @GetMapping("/delete")
    public Result delete(@RequestParam Integer stockInId) {
        return stockInService.removeById(stockInId) ? Result.success() : Result.fail();
    }

    //   新增方法：根据入库单 ID 查询入库单详情，包含关联的采购订单信息
    @GetMapping("/details/{stockInId}")
    public Result getStockInWithDetails(@PathVariable Integer stockInId) {
        StockIn stockIn = stockInService.getStockInWithDetails(stockInId);
        if (stockIn != null) {
            return Result.success(stockIn);
        } else {
            return Result.fail();
        }
    }

    //   新增方法：根据入库单 ID 查询明细，并包含商品信息
    @GetMapping("/items/{stockInId}")
    public Result getStockInItemsWithProductInfo(@PathVariable Integer stockInId) {
        List<StockInItem> items = stockInItemService.getStockInItemsWithProductInfo(stockInId);
        return Result.success(items);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Map<String, Object> data) {
        Integer stockInId = (Integer) data.get("stockInId");
        String status = (String) data.get("status");

        boolean success = stockInService.updateStatusById(stockInId, status);
        return success ? Result.success() : Result.fail();
    }


}
