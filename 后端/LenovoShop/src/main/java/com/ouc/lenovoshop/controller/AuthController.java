package com.ouc.lenovoshop.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ouc.lenovoshop.common.Result;
import com.ouc.lenovoshop.common.enums.ResultCodeEnum;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.User;
import com.ouc.lenovoshop.service.BusinessService;
import com.ouc.lenovoshop.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private JavaMailSender mailSender;

    @Resource
    private UserService userService;

    @Resource
    private BusinessService businessService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final Map<String, String> CODE_MAP = new ConcurrentHashMap<>();

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        if (ObjectUtil.isEmpty(account.getUsername()) || ObjectUtil.isEmpty(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.BUSINESS.name().equals(account.getRole())) {
            account = businessService.login(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            account = userService.login(account);
        }
        return Result.success(account);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getRole())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.register(account);
        }
        return Result.success();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if (StrUtil.isBlank(account.getUsername()) || StrUtil.isBlank(account.getPassword())
                || ObjectUtil.isEmpty(account.getNewPassword())) {
            return Result.error(ResultCodeEnum.PARAM_LOST_ERROR);
        }
        if (RoleEnum.BUSINESS.name().equals(account.getRole())) {
            businessService.updatePassword(account);
        }
        if (RoleEnum.USER.name().equals(account.getRole())) {
            userService.updatePassword(account);
        }
        return Result.success();
    }

    /**
     * 发送验证码
     */
    @PostMapping("/sendEmailCode")
    public Result sendEmailCode(@RequestParam String email) {
        if (StrUtil.isBlank(email)) {
            return Result.error("邮箱不能为空");
        }
        
        User dbUser = userService.selectByEmail(email);
        if (dbUser == null) {
            return Result.error("该邮箱未注册");
        }

        String code = RandomUtil.randomNumbers(6);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("【联想商城】密码重置验证");
            message.setText("您的验证码是：" + code + "。");
            mailSender.send(message);

            CODE_MAP.put(email, code);
            return Result.success("验证码发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邮件发送异常");
        }
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String code = payload.get("code");
        String newPassword = payload.get("newPassword");

        if (StrUtil.hasBlank(email, code, newPassword)) {
            return Result.error("参数缺失");
        }

        String correctCode = CODE_MAP.get(email);
        if (correctCode == null || !correctCode.equals(code)) {
            return Result.error("验证码错误");
        }

        User user = userService.selectByEmail(email);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setPassword(newPassword);
        userService.updateById(user);

        CODE_MAP.remove(email);

        return Result.success("密码重置成功");
    }
}
