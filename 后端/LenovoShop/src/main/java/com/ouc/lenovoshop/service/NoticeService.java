package com.ouc.lenovoshop.service;

import cn.hutool.core.date.DateUtil;
import com.ouc.lenovoshop.entity.Account;
import com.ouc.lenovoshop.entity.Notice;
import com.ouc.lenovoshop.mapper.NoticeMapper;
import com.ouc.lenovoshop.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 公告信息表业务处理
 **/
@Service
public class NoticeService {

    @Resource
    private NoticeMapper noticeMapper;

    /**
     * 新增
     */
    public void add(Notice notice) {
        // 设置时间为详细时间（包括时分秒），便于展示到前端
        notice.setTime(DateUtil.now());
        Account currentUser = TokenUtils.getCurrentUser();
        notice.setUser(currentUser.getUsername());
        // 将 businessId 设为商家 id；若当前用户不是商家，则回退到 business id=1
        if ("BUSINESS".equals(currentUser.getRole())) {
            notice.setBusinessId(currentUser.getId());
        } else {
            notice.setBusinessId(1);
        }
        noticeMapper.insert(notice);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        noticeMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            noticeMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Notice notice) {
        // 如果没有 businessId，设定为 businessId=1 以通过 NOT NULL 约束
        if (notice.getBusinessId() == null) {
            notice.setBusinessId(1);
        }
        noticeMapper.updateById(notice);
    }

    /**
     * 根据ID查询
     */
    public Notice selectById(Integer id) {
        return noticeMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }

    /**
     * 分页查询
     */
    public PageInfo<Notice> selectPage(Notice notice, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = noticeMapper.selectAll(notice);
        return PageInfo.of(list);
    }

}