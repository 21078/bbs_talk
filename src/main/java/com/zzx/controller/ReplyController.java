package com.zzx.controller;


import com.zzx.model.User;
import com.zzx.service.ReplyService;
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

    /**
     * 删除回复接口（管理员权限）
     * 管理员可以删除任意回复
     *
     * @param rid 回复ID
     * @param session HTTP会话对象
     * @return 删除结果消息
     */
    @RequestMapping(value = "/deleteReply/{rid}", method = RequestMethod.GET)
    @ResponseBody
    public String deleteReply(@PathVariable Long rid, HttpSession session) {

        User user = (User)session.getAttribute("user");
        // 检查用户是否为管理员
        if (null != user && user.getLevel() == 0) {
            replyService.deleteReplyRid(rid);
            return "删除成功";
        }
        return "能不能删你心里没B数？";


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
            return replyService.toggleReplySticky(rid, user.getUid().longValue(), pid, action);
        }
        return "未登录";
    }
}
