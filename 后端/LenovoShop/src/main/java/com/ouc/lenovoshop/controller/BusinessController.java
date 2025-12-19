package com.ouc.lenovoshop.controller;

import com.ouc.lenovoshop.common.Result;
import com.ouc.lenovoshop.entity.Business;
import com.ouc.lenovoshop.service.BusinessService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商家前端操作接口
 **/
@RestController
@RequestMapping("/business")
public class BusinessController {

    @Resource
    private BusinessService businessService;

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Business business) {
        businessService.updateById(business);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Business business = businessService.selectById(id);
        return Result.success(business);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Business business ) {
        List<Business> list = businessService.selectAll(business);
        return Result.success(list);
    }

}