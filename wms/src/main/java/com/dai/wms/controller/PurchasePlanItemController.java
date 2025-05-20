package com.dai.wms.controller;



import com.dai.wms.common.Result;
import com.dai.wms.entity.PurchasePlanItem;

import com.dai.wms.service.PurchasePlanItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-04-22
 */
@RestController
@RequestMapping("/purchase-plan-item")
public class PurchasePlanItemController {
    @Autowired
    private PurchasePlanItemService purchasePlanItemService;

    @GetMapping("/list")
    public Result getAllPlanItemsWithProductInfo() {
        List<PurchasePlanItem> items = purchasePlanItemService.getAllPlanItemsWithProductInfo();
        return Result.success(items);
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody PurchasePlanItem purchasePlanItem) {
        return purchasePlanItemService.save(purchasePlanItem)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return purchasePlanItemService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody PurchasePlanItem purchasePlanItem) {
        return purchasePlanItemService.updateById(purchasePlanItem)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody PurchasePlanItem purchasePlanItem) {
        return purchasePlanItemService.saveOrUpdate(purchasePlanItem);
    }

//    // 查 模糊查询
//    @PostMapping("/listP")
//    public Result listP(@RequestBody PurchasePlanItem purchasePlanItem) {
//        LambdaQueryWrapper<PurchasePlanItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(purchasePlanItem.getName())) {
//            lambdaQueryWrapper.like(PurchasePlanItem::getName, purchasePlanItem.getName());
//        }
//        return Result.success(purchasePlanItemService.list(lambdaQueryWrapper));
//    }
//
//    // 分页查询
//    @PostMapping("/listPage")
//    public Result listPage(@RequestBody QueryPageParam query) {
//        HashMap param = query.getParam();
//        String name = (String) param.get("name");
//        String sex = (String) param.get("sex");
//        String roleId = (String) param.get("roleId");
//
//        Page<PurchasePlanItem> page = new Page<>();
//        page.setCurrent(query.getPageNum());
//        page.setSize(query.getPageSize());
//
//        LambdaQueryWrapper<PurchasePlanItem> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
//            lambdaQueryWrapper.like(PurchasePlanItem::getName, name);
//        }
//        if(StringUtils.isNotBlank(sex)) {
//            lambdaQueryWrapper.eq(PurchasePlanItem::getSex, sex);
//        }
//        if(StringUtils.isNotBlank(roleId)) {
//            lambdaQueryWrapper.eq(PurchasePlanItem::getRoleId, roleId);
//        }
//        IPage res = purchasePlanItemService.page(page, lambdaQueryWrapper);
//
//        return Result.success(res.getTotal(), res.getRecords());
//    }
//
//    //按照id查询
//    @GetMapping("/findByNo")
//    public Result findByNo(@RequestParam String no) {
//        List<PurchasePlanItem> list = purchasePlanItemService.lambdaQuery().eq(PurchasePlanItem::getNo, no).list();
//        return list.size()>0 ? Result.success(list) : Result.fail();
//    }

}
