package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.StockOut;
import com.dai.wms.entity.StockOutItem;
import com.dai.wms.service.StockOutItemService;
import com.dai.wms.service.StockOutService;
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
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/stock-out")
public class StockOutController {
    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private StockOutItemService stockOutItemService;

    @GetMapping("/list")
    public Result list() {
        List<StockOut> stockOutListWithSaleDate = stockOutService.getStockOutListWithSaleDate();
        return Result.success(stockOutListWithSaleDate);
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody StockOut stockOut) {
        boolean stockOutSaved = stockOutService.saveStockOutWithItems(stockOut);
        return stockOutSaved ? Result.success() : Result.fail();
    }


    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return stockOutService.removeById(id) ? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result modify(@RequestBody StockOut stockOut) {
        boolean stockOutUpdated = stockOutService.updateStockOutWithItems(stockOut);

        if (stockOutUpdated) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody StockOut stockOut) {
        return stockOutService.saveOrUpdate(stockOut);
    }

    // 根据ID查询
    @GetMapping("/{stockOutId}")
    public Result getStockOut(@PathVariable Integer stockOutId) {
        StockOut stockOut = stockOutService.findByIdWithSaleDate(stockOutId);
        if (stockOut != null) {
            return Result.success(stockOut);
        } else {
            return Result.fail();
        }
    }

    // 根据出库单ID查询明细
    @GetMapping("/items/{stockOutId}")
    public Result getStockOutItemsWithProductInfo(@PathVariable Integer stockOutId) {
        List<StockOutItem> itemsWithProductInfo = stockOutItemService.getStockOutItemsWithProductInfo(stockOutId);
        return Result.success(itemsWithProductInfo);
    }

    @PostMapping("/updateStatus")
    public Result updateStatus(@RequestBody Map<String, Object> data) {
        Integer stockOutId = (Integer) data.get("stockOutId");
        String status = (String) data.get("status");

        boolean success = stockOutService.updateStatusById(stockOutId, status);
        return success ? Result.success() : Result.fail();
    }

}
