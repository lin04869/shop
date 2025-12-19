package com.ouc.lenovoshop.service;

import cn.hutool.core.util.ObjectUtil;
import com.ouc.lenovoshop.common.Constants;
import com.ouc.lenovoshop.common.enums.ResultCodeEnum;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.Business;
import com.ouc.lenovoshop.exception.CustomException;
import com.ouc.lenovoshop.mapper.BusinessMapper;
import com.ouc.lenovoshop.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商家业务处理
 **/
@Service
public class BusinessService {

    @Resource
    private BusinessMapper businessMapper;
    @Resource
    private com.ouc.lenovoshop.mapper.UserMapper userMapper;

    /**
     * 修改
     */
    public void updateById(Business business) {
        businessMapper.updateById(business);
    }

    /**
     * 根据ID查询
     */
    public Business selectById(Integer id) {
        return businessMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Business> selectAll(Business business) {
        return businessMapper.selectAll(business);
    }

    /**
     * 登录
     */
    public Account login(Account account) {
        Account dbBusiness = businessMapper.selectByUsername(account.getUsername());
        // 准备变量以统一构造 token：operatorId(登录用户), shopId(商家店铺 id), sign(签名用密码)
        Integer operatorId = null;
        Integer shopId = null;
        String signPassword = null;
        Account ret = null;
        if (ObjectUtil.isNull(dbBusiness)) {
            com.ouc.lenovoshop.entity.User u = userMapper.selectByUsername(account.getUsername());
            if (ObjectUtil.isNull(u)) {
                throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }
            List<Business> shops = businessMapper.selectAll(new Business());
            if (shops == null || shops.isEmpty()) {
                throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
            }
            Business shop = shops.get(0);
            operatorId = u.getId();
            shopId = shop.getId();
            signPassword = u.getPassword();
            // 构造返回给前端的对象（以商家身份展示）
            ret = new Account();
            ret.setId(shopId);
            ret.setUsername(u.getUsername());
            ret.setPassword(signPassword);
        } else {
            // business 表中找到对应记录
            Business b = (Business) dbBusiness;
            shopId = b.getId();
            if (ObjectUtil.isEmpty(b.getPassword())) {
                // business 表没有密码，使用 user 表中的密码作为签名源
                com.ouc.lenovoshop.entity.User u = userMapper.selectByUsername(account.getUsername());
                if (ObjectUtil.isNull(u)) {
                    throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
                }
                if (!account.getPassword().equals(u.getPassword())) {
                    throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
                }
                operatorId = u.getId();
                signPassword = u.getPassword();
            } else {
                // business 表本身有密码
                if (!account.getPassword().equals(b.getPassword()) &&
                        !cn.hutool.crypto.SecureUtil.md5(account.getPassword()).equals(b.getPassword())) {
                    throw new CustomException(ResultCodeEnum.USER_ACCOUNT_ERROR);
                }
                operatorId = b.getId();
                signPassword = b.getPassword();
            }
            // 构造返回给前端的对象
            ret = new Account();
            ret.setId(shopId);
            ret.setUsername(b.getUsername());
            ret.setPassword(signPassword);
        }
        // 生成token，格式 operatorId-shopId-role
        String tokenData = operatorId + "-" + shopId + "-" + RoleEnum.BUSINESS.name();
        String token = TokenUtils.createToken(tokenData, signPassword);
        ret.setToken(token);
        ret.setRole(RoleEnum.BUSINESS.name());
        return ret;
    }

    /**
     * 修改密码
     */
    public void updatePassword(Account account) {
        Business dbBusiness = businessMapper.selectByUsername(account.getUsername());
        if (ObjectUtil.isNull(dbBusiness)) {
            throw new CustomException(ResultCodeEnum.USER_NOT_EXIST_ERROR);
        }
        if (!account.getPassword().equals(dbBusiness.getPassword())) {
            throw new CustomException(ResultCodeEnum.PARAM_PASSWORD_ERROR);
        }
        dbBusiness.setPassword(account.getNewPassword());
        businessMapper.updateById(dbBusiness);
    }

}