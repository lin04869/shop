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
            if (RoleEnum.BUSINESS.name().equals(role)) {
                account = businessService.selectById(Integer.valueOf(subjectId));
                if (ObjectUtil.isNull(account)) {
                    throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
                }
                boolean verified = false;
                // 1. 优先尝试验证 User (兼容旧逻辑或 ID 冲突时的 User)
                Account userOperator = userService.selectById(Integer.valueOf(operatorId));
                if (ObjectUtil.isNotNull(userOperator)) {
                    try {
                        JWT.require(Algorithm.HMAC256(userOperator.getPassword())).build().verify(token);
                        account.setPassword(userOperator.getPassword());
                        verified = true;
                    } catch (Exception e) {
                        // 验证失败，可能是 ID 冲突，继续尝试 Business
                    }
                }
                // 2. 如果 User 验证失败，尝试验证 Business (Admin 场景)
                if (!verified) {
                    Account businessOperator = businessService.selectById(Integer.valueOf(operatorId));
                    if (ObjectUtil.isNotNull(businessOperator)) {
                        try {
                            JWT.require(Algorithm.HMAC256(businessOperator.getPassword())).build().verify(token);
                            account.setPassword(businessOperator.getPassword());
                            verified = true;
                        } catch (Exception e) {
                            // 验证失败
                        }
                    }
                }
                // 如果都未验证通过，抛出异常
                if (!verified) {
                    throw new CustomException(ResultCodeEnum.TOKEN_CHECK_ERROR);
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