package com.ouc.lenovoshop.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ouc.lenovoshop.common.Constants;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.service.BusinessService;
import com.ouc.lenovoshop.service.UserService;
import com.ouc.lenovoshop.entity.Business;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Token工具类
 */
@Component
public class TokenUtils {

    private static final Logger log = LoggerFactory.getLogger(TokenUtils.class);

    private static BusinessService staticBusinessService;
    private static UserService staticUserService;
    @Resource
    BusinessService businessService;
    @Resource
    UserService userService;
    @PostConstruct
    public void setUserService() {
        staticBusinessService = businessService;
        staticUserService = userService;
    }
    /**
     * 生成token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 将 userId-role 保存到 token 里面,作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2)) // 2小时后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息
     */
    public static Account getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader(Constants.TOKEN);
            if (ObjectUtil.isNotEmpty(token)) {
                    String userRole = JWT.decode(token).getAudience().get(0);
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
                        return new Account();
                    }
                    if (RoleEnum.BUSINESS.name().equals(role)) {
                        // 返回 subjectId 所表示的 shop 作为当前用户（方便业务层查询商家数据）
                        Account business = staticBusinessService.selectById(Integer.valueOf(subjectId));
                        if (business != null) {
                            business.setRole(RoleEnum.BUSINESS.name());
                        }
                        return business;
                    }
                    if (RoleEnum.USER.name().equals(role)) {
                        Account user = staticUserService.selectById(Integer.valueOf(operatorId));
                        if (user != null) {
                            user.setRole(RoleEnum.USER.name());
                        }
                        return user;
                    }
            }
        } catch (Exception e) {
            log.error("获取当前用户信息出错", e);
        }
        return new Account();  // 返回空的账号对象
    }
}

