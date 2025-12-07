package com.ouc.lenovoshop.service;

import cn.hutool.core.date.DateUtil;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.Cart;
import com.ouc.lenovoshop.entity.Orders;
import com.ouc.lenovoshop.mapper.CartMapper;
import com.ouc.lenovoshop.mapper.OrdersMapper;
import com.ouc.lenovoshop.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.management.relation.Role;
import java.util.Date;
import java.util.List;

/**
 * 收藏业务处理
 **/
@Service
public class OrdersService {

    @Resource
    private OrdersMapper ordersMapper;
    @Resource
    private CartMapper cartMapper;

    /**
     * 新增
     */
    public void add(Orders orders) {
        orders.setOrderId(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        for (Cart cart : orders.getCartData()) {
            Orders dbOrders = new Orders();
            BeanUtils.copyProperties(orders, dbOrders);
            dbOrders.setGoodsId(cart.getGoodsId());
            dbOrders.setBusinessId(cart.getBusinessId());
            dbOrders.setNum(cart.getNum());
            dbOrders.setPrice(cart.getNum() * cart.getGoodsPrice());
            // 将中文状态转换为数据库期望的整数值（tinyint）
            String statusStr = dbOrders.getStatus();
            String mappedStatus = mapStatusToNumber(statusStr);
            dbOrders.setStatus(mappedStatus);
            ordersMapper.insert(dbOrders);

            // 把购物车里对应的商品删掉
            cartMapper.deleteById(cart.getId());
        }
    }

    /**
     * 把前端传来的状态文本映射为数据库中 tinyint 能接受的数字字符串
     */
    private String mapStatusToNumber(String status) {
        if (status == null) return "0";
        status = status.trim();
        switch (status) {
            case "待付款":
                return "0";
            case "待发货":
                return "1";
            case "待收货":
                return "2";
            case "已完成":
                return "3";
            case "已评价":
                return "3";
            default:
                // 如果已经是数字字符串，直接返回；否则默认 0
                try {
                    Integer.parseInt(status);
                    return status;
                } catch (NumberFormatException e) {
                    return "0";
                }
        }
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        ordersMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            ordersMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Orders orders) {
        // 如果前端传入中文状态文本，映射为 numeric 字符串以写入数据库的 tinyint 字段
        if (orders != null && orders.getStatus() != null) {
            String mapped = mapStatusToNumber(orders.getStatus());
            orders.setStatus(mapped);
        }
        ordersMapper.updateById(orders);
    }

    /**
     * 根据ID查询
     */
    public Orders selectById(Integer id) {
        return ordersMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Orders> selectAll(Orders orders) {
        return ordersMapper.selectAll(orders);
    }

    /**
     * 分页查询
     */
    public PageInfo<Orders> selectPage(Orders orders, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (orders == null) {
            orders = new Orders();
        }
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            orders.setUserId(currentUser.getId());
        }
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            orders.setBusinessId(currentUser.getId());
        }
        // 如果前端传入中文状态文本，则转换为 number 字符串来查询
        if (orders.getStatus() != null && !orders.getStatus().matches("\\d+")) {
            orders.setStatus(mapStatusToNumber(orders.getStatus()));
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Orders> list = ordersMapper.selectAll(orders);
        return PageInfo.of(list);
    }
}