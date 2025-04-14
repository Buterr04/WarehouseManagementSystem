package com.dai.wms.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dai.wms.common.QueryPageParam;
import com.dai.wms.common.Result;
import com.dai.wms.entity.User;
import com.dai.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dai
 * @since 2025-04-13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public List<User> list() {
        return userService.list();
    }

    // 增
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        return userService.save(user)? Result.success() : Result.fail();
    }

    // 删
    @GetMapping("/delete")
    public Result delete(@RequestParam String id) {
        return userService.removeById(id)? Result.success() : Result.fail();
    }

    // 改
    @PostMapping("/modify")
    public Result mod(@RequestBody User user) {
        return userService.updateById(user)? Result.success() : Result.fail();
    }

    // 新增或修改
    @PostMapping("saveOrModify")
    public boolean saveOrModify(@RequestBody User user) {
        return userService.saveOrUpdate(user);
    }

    // 查 模糊查询
    @PostMapping("/listP")
    public Result listP(@RequestBody User user) {
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(user.getName())) {
            lambdaQueryWrapper.like(User::getName, user.getName());
        }
        return Result.success(userService.list(lambdaQueryWrapper));
    }

    // 分页查询
    @PostMapping("/listPage")
    public Result listPage(@RequestBody QueryPageParam query) {
        HashMap param = query.getParam();
        String name = (String) param.get("name");
        String sex = (String) param.get("sex");
        String roleId = (String) param.get("roleId");

        Page<User> page = new Page<>();
        page.setCurrent(query.getPageNum());
        page.setSize(query.getPageSize());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(name) && !name.equals("null")) {
            lambdaQueryWrapper.like(User::getName, name);
        }
        if(StringUtils.isNotBlank(sex)) {
            lambdaQueryWrapper.eq(User::getSex, sex);
        }
        if(StringUtils.isNotBlank(roleId)) {
            lambdaQueryWrapper.eq(User::getRoleId, roleId);
        }
        IPage res = userService.page(page, lambdaQueryWrapper);

        return Result.success(res.getTotal(), res.getRecords());
    }

    //按照id查询
    @GetMapping("/findByNo")
    public Result findByNo(@RequestParam String no) {
        List<User> list = userService.lambdaQuery().eq(User::getNo, no).list();
        return list.size()>0 ? Result.success(list) : Result.fail();
    }
}
