package com.ouc.lenovoshop.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ouc.lenovoshop.common.enums.ResultCodeEnum;
import com.ouc.lenovoshop.common.enums.RoleEnum;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.Comment;
import com.ouc.lenovoshop.exception.CustomException;
import com.ouc.lenovoshop.mapper.CommentMapper;
import com.ouc.lenovoshop.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private com.ouc.lenovoshop.service.BusinessService businessService;

    /**
     * 新增
     */
    public void add(Comment comment) {
        comment.setTime(DateUtil.now());
        commentMapper.insert(comment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (comment == null) {
            comment = new Comment();
        }
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            comment.setUserId(currentUser.getId());
        }
        if (RoleEnum.BUSINESS.name().equals(currentUser.getRole())) {
            comment.setBusinessId(currentUser.getId());
        }
        // 兼容单店场景：若没有商家角色但未指定 businessId，则回退到第一个商家
        if (!RoleEnum.BUSINESS.name().equals(currentUser.getRole()) && comment.getBusinessId() == null) {
            try {
                java.util.List<com.ouc.lenovoshop.entity.Business> shops = businessService.selectAll(new com.ouc.lenovoshop.entity.Business());
                if (shops != null && !shops.isEmpty()) {
                    comment.setBusinessId(shops.get(0).getId());
                }
            } catch (Exception ignored) {
            }
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        return PageInfo.of(list);
    }

    public List<Comment> selectByGoodsId(Integer id) {
        return commentMapper.selectByGoodsId(id);
    }
}