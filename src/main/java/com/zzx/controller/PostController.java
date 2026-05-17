package com.zzx.controller;


import com.zzx.model.Favorite;
import com.zzx.model.Post;

import com.zzx.model.Reply;
import com.zzx.model.User;
import com.zzx.service.PostService;
import com.zzx.service.ReplyService;
import com.zzx.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 帖子控制器
 * 处理帖子相关的HTTP请求，包括发帖、查看帖子、回复等
 */
@Controller
public class PostController {


    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 发布帖子接口
     * 处理用户发布新帖子的请求
     *
     * @param session HTTP会话对象
     * @param post 帖子对象，包含标题和内容
     * @return 发布结果消息
     */
    @RequestMapping(value = "/sendPost.do", method = RequestMethod.POST)
    @ResponseBody
    public String sendPost(HttpSession session, Post post, String category) {
        User user = (User)session.getAttribute("user");
        if (null != user) {
            // 检查用户是否被禁言
            if (user.getUstate() == 0)
                return "你已被禁言";

            // 预设板块列表
            List<String> validCategories = Arrays.asList("娱乐", "技术", "美食", "旅游", "问题");

            // 验证帖子标题、内容和板块
            if ((post.getPtitle().length() > 0 && post.getPtitle().length() <= 30) &&
                (post.getPbody().length() > 0 && post.getPbody().length() < 1000) &&
                (category != null && validCategories.contains(category))) {
                // 处理换行符，转换为HTML格式
                post.setPbody(post.getPbody().replaceAll("\n", "<br />"));
                post.setUser(user);
                post.setCategory(category);

                // 设置发帖时间和最后回复时间
                Date date = new Date();
                post.setPsendtime(date);
                post.setLastreplytime(date);

                postService.save(post);
                return "发送成功";
            } else
                return "注意字数和板块选择不正确";
        } else
            return "未登录";
    }

    /**
     * 帖子详情页面
     * 显示指定帖子的内容和回复列表
     *
     * @param pid 帖子ID
     * @param page 页码参数
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 帖子详情页面视图
     */
    @RequestMapping(value = {"/post/{pid}.html"}, method = RequestMethod.GET)
    public String replyPage(@PathVariable Long pid, @RequestParam(value = "page", required = false) Long page, Model model, HttpSession session) {

        // 根据pid查询帖子信息
        Post post = postService.findPostByPid(pid);

        // 检查当前用户是否已收藏该帖子
        User user = (User) session.getAttribute("user");
        if (user != null) {
            Favorite existing = favoriteService.findByUserAndPost(user.getUid(), pid);
            post.setIsFavorited(existing != null);
        } else {
            post.setIsFavorited(false);
        }

        model.addAttribute("post", post);

        // 根据pid分页查询回复
        Map<String, Long> map = new HashMap<>();
        map.put("pid", pid);
        map.put("startPage", page == null ? 0L : page - 1);
        model.addAttribute("page", replyService.findReplyByPidAndPage(map));

        return "post";
    }

    /**
     * 回复帖子接口
     * 处理用户对帖子的回复请求
     *
     * @param reply 回复对象，包含回复内容
     * @param session HTTP会话对象
     * @return 回复结果消息
     */
    @RequestMapping(value = "/reply.do", method = RequestMethod.POST)
    @ResponseBody
    public String reply(Reply reply, HttpSession session) {

        User user = (User)session.getAttribute("user");
        if (null != user) {
            // 检查用户是否被禁言
            if (user.getUstate() == 0)
                return "你已被禁言";

            // 验证回复内容长度
            if (reply.getReplymessage().length() > 0 && reply.getReplymessage().length() <= 1000) {
                // 处理换行符，转换为HTML格式
                reply.setReplymessage(reply.getReplymessage().replaceAll("\n", "<br />"));

                // 设置回复时间和用户信息
                Date date = new Date();
                reply.setUser(user);
                reply.setReplytime(date);

                // 更新帖子的最后回复时间
                reply.getPost().setLastreplytime(date);

                replyService.saveReply(reply);
                return "回帖成功";
            } else
                return "注意字数";
        } else
            return "未登录";
    }

    /**
     * 删除帖子接口
     * 删除指定帖子（需要管理员权限或帖子作者身份）
     *
     * @param pid 帖子ID
     * @param session HTTP会话对象
     * @return 重定向到首页
     */
    @RequestMapping(value = "/delete/{pid}")
    public String deletePost(@PathVariable Long pid, HttpSession session) {

        User user = (User)session.getAttribute("user");
        Post post = postService.findPostByPid(pid);

        // 检查权限：管理员或帖子作者可以删除
        if ((null != user && user.getLevel() == 0) || user.getUid() == post.getUser().getUid()) {
            // 禁言状态不能删除帖子
            if (user.getUstate() != 0)
                postService.deletePost(pid);
        }
        return "redirect:/";
    }

    /**
     * 我的帖子页面
     * 显示当前用户发布的所有帖子
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 我的帖子页面视图
     */
    @RequestMapping(value = "/myPosts.do", method = RequestMethod.GET)
    public String myPosts(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user != null) {
            List<Post> myPosts = postService.findPostsByUserId(user.getUid().longValue());
            model.addAttribute("myPosts", myPosts);
            return "my_posts";
        }
        return "redirect:/";
    }

    /**
     * 更新帖子内容接口
     * 允许用户修改自己帖子的内容（不能修改标题）
     *
     * @param post 帖子对象，包含新的内容
     * @param session HTTP会话对象
     * @return 更新结果消息
     */
    @RequestMapping(value = "/updatePostContent.do", method = RequestMethod.POST)
    @ResponseBody
    public String updatePostContent(Post post, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user != null) {
            // 验证内容长度
            if (post.getPbody().length() > 0 && post.getPbody().length() < 1000) {
                // 处理换行符，转换为HTML格式
                post.setPbody(post.getPbody().replaceAll("\n", "<br />"));
                post.setUser(user);
                postService.updatePostContent(post);
                return "更新成功";
            } else {
                return "内容长度不符合要求";
            }
        }
        return "未登录";
    }

    /**
     * 切换帖子置顶状态接口
     * 管理员可以将帖子置顶或取消置顶
     *
     * @param pid 帖子ID
     * @param action 操作：sticky置顶，unsticky取消置顶
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @RequestMapping(value = "/toggleSticky/{pid}/{action}", method = RequestMethod.GET)
    @ResponseBody
    public String toggleSticky(@PathVariable Long pid, @PathVariable String action, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user != null && user.getLevel() == 0) { // 只有管理员可以操作
            if ("sticky".equals(action)) {
                postService.toggleSticky(pid, 1);
                return "置顶成功";
            } else if ("unsticky".equals(action)) {
                postService.toggleSticky(pid, 0);
                return "取消置顶成功";
            } else {
                return "操作参数错误";
            }
        }
        return "权限不足或未登录";
    }

}
