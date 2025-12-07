package com.ouc.lenovoshop.controller;

import com.ouc.lenovoshop.common.Result;
import com.ouc.lenovoshop.entity.Comment;
import com.ouc.lenovoshop.service.CommentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import com.ouc.lenovoshop.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论前端操作接口
 **/
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        commentService.add(comment);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        commentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Comment comment) {
        commentService.updateById(comment);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Comment comment = commentService.selectById(id);
        return Result.success(comment);
    }

    @GetMapping("/selectByGoodsId")
    public Result selectByGoodsId(@RequestParam Integer id) {
        List<Comment> list =  commentService.selectByGoodsId(id);
        return Result.success(list);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Comment comment ) {
        List<Comment> list = commentService.selectAll(comment);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Comment comment,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            javax.servlet.http.HttpServletRequest req = ((org.springframework.web.context.request.ServletRequestAttributes)org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes()).getRequest();
            String token = req.getHeader(com.ouc.lenovoshop.common.Constants.TOKEN);
            com.ouc.lenovoshop.entity.Account current = TokenUtils.getCurrentUser();
            log.info("CommentController.selectPage token={} currentUser.id={} role={}", token, current.getId(), current.getRole());
        } catch (Exception e) {
            log.warn("CommentController.selectPage debug log failed", e);
        }
        PageInfo<Comment> page = commentService.selectPage(comment, pageNum, pageSize);
        return Result.success(page);
    }

}