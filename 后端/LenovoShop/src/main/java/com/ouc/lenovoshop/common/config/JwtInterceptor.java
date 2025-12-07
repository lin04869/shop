package com.ouc.lenovoshop.common.config;

import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.ouc.lenovoshop.common.Constants;
import com.ouc.lenovoshop.common.enums.ResultCodeEnum;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.exception.CustomException;
import com.ouc.lenovoshop.service.AdminService;
import com.ouc.lenovoshop.service.BusinessService;
import com.ouc.lenovoshop.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt拦截器
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private AdminService adminService;
    @Resource
    private BusinessService businessService;
    @Resource
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 1. 从http请求的header中获取token
        String token = request.getHeader(Constants.TOKEN);
        if (ObjectUtil.isEmpty(token)) {
            // 如果没拿到，从参数里再拿一次
            token = request.getParameter(Constants.TOKEN);
        }
        // 2. 开始执行认证
        if (ObjectUtil.isEmpty(token)) {
            throw new CustomException(ResultCodeEnum.TOKEN_INVALID_ERROR);
        }
        Account account = null;
        try {
            // 解析token获取存储的数据
            String userRole = JWT.decode(token).getAudience().get(0);
            // 支持两种格式：
            // 1) operatorId-shopId-role (operatorId and subjectId present)
            // 2) id-role (legacy)
            String[] parts = userRole.split("-");
            String operatorId;
            String subjectId;
            String role;
            if (parts.length == 3) {
                operatorId = parts[0];
                subjectId = parts[1];
                role = parts[2];
            } else if (parts.length == 2) {
                operatorId = parts[0];
                subjectId = parts[0];
                role = parts[1];
            } else {
                throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
            }
            // 根据 operatorId 用于校验签名（operator为实际的登录账号）
            if (RoleEnum.ADMIN.name().equals(role)) {
                account = adminService.selectById(Integer.valueOf(operatorId));
            }
            if (RoleEnum.BUSINESS.name().equals(role)) {
                // 1. 获取用于验证的 operator（登录账号）
                com.ouc.lenovoshop.entity.Account operator = userService.selectById(Integer.valueOf(operatorId));
                // 如果 operator 不存在，尝试以 operatorId 为商家 id 回退（兼容性）
                if (ObjectUtil.isNull(operator)) {
                    // fallback: operator may be a business account
                    operator = businessService.selectById(Integer.valueOf(operatorId));
                }
                account = businessService.selectById(Integer.valueOf(subjectId));
                // 将 operator 的密码临时放入 account 中，方便下面统一校验（如果 operator 存在且其密码不为空）
                if (ObjectUtil.isNotNull(operator) && ObjectUtil.isNotEmpty(operator.getPassword())) {
                    account.setPassword(operator.getPassword());
                }
            }
            if (RoleEnum.USER.name().equals(role)) {
                account = userService.selectById(Integer.valueOf(operatorId));
            }
        } catch (Exception e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        if (ObjectUtil.isNull(account)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        try {
            // 用户密码加签验证 token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token); // 验证token
        } catch (JWTVerificationException e) {
            throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
        }
        return true;
    }
}