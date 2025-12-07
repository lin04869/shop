package com.ouc.lenovoshop.controller;

import com.ouc.lenovoshop.common.Result;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.User;
import com.ouc.lenovoshop.service.UserService;
import com.ouc.lenovoshop.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/business/member")
public class BusinessMemberController {

    @Resource
    private UserService userService;

    @PostMapping("/join")
    public Result join() {
        Account current = TokenUtils.getCurrentUser();
        if (current == null || current.getId() == null) {
            return Result.error("401", "请先登录");
        }
        // 仅允许用户角色加入
        User u = new User();
        u.setId(current.getId());
        u.setIsMember(1);
        u.setMemberSince(new Date());
        userService.updateById(u);
        return Result.success();
    }

    //商家端查看会员列表
    @GetMapping("/list")
    public Result list(User user, @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        user.setIsMember(1);
        PageInfo<User> page = userService.selectPage(user, pageNum, pageSize);
        return Result.success(page);
    }
}
