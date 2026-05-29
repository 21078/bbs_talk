package com.zzx.controller;


import com.zzx.model.User;
import com.zzx.service.ReplyService;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 回复控制器
 * 处理回复相关的HTTP请求，主要提供删除回复功能
 */
@Controller
public class ReplyController {


    @Autowired
    private ReplyService replyService;

    @Autowired
    private UserService userService;

    /**
     * 删除回复接口
     * 支持三种权限：
     * 1. 管理员可以删除任意回复
     * 2. 帖子创建者可以删除该帖子的所有回复
     * 3. 回复创建者可以删除自己的回复
     *
     * @param rid 回复ID
     * @param session HTTP会话对象
     * @return 删除结果消息
     */
    @RequestMapping(value = "/deleteReply/{rid}", method = RequestMethod.GET)
    @ResponseBody
    public String deleteReply(@PathVariable Long rid, HttpSession session) {
        User user = (User)session.getAttribute("user");

        if (user == null) {
            return "未登录";
        }

        // 检查权限：管理员可以删除任意回复
        if (user.getLevel() == 0) {
            replyService.deleteReplyRid(rid);
            return "删除成功";
        }

        // 检查是否是回复创建者
        if (replyService.isReplyCreator(rid, user.getUid().longValue())) {
            replyService.deleteReplyRid(rid);
            return "删除成功";
        }

        // 检查是否是帖子创建者
        if (replyService.isPostCreatorByReplyId(rid, user.getUid().longValue())) {
            replyService.deleteReplyRid(rid);
            return "删除成功";
        }

        return "没有权限删除该回复";
    }

    /**
     * 切换回复置顶状态接口
     * 只有帖子创建者可以操作回复置顶
     *
     * @param rid 回复ID
     * @param pid 帖子ID
     * @param action 操作：sticky置顶，unsticky取消置顶
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @RequestMapping(value = "/toggleReplySticky/{rid}/{pid}/{action}", method = RequestMethod.GET)
    @ResponseBody
    public String toggleReplySticky(@PathVariable Long rid, @PathVariable Long pid, @PathVariable String action, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user != null) {
            String result = replyService.toggleReplySticky(rid, user.getUid().longValue(), pid, action);

            // 如果操作成功且是置顶操作，更新session中的用户信息
            if (result.contains("成功")) {
                // 重新获取用户信息以确保session中的数据是最新的
                User updatedUser = userService.findUserByUid(user.getUid());
                if (updatedUser != null) {
                    session.setAttribute("user", updatedUser);
                }
            }

            return result;
        }
        return "未登录";
    }
}
