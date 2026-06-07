package com.zzx.controller;


import com.zzx.model.Favorite;
import com.zzx.model.Post;

import com.zzx.model.Reply;
import com.zzx.model.User;
import com.zzx.service.PostService;
import com.zzx.service.ReplyService;
import com.zzx.service.FavoriteService;
import com.zzx.service.NotificationService;
import com.zzx.service.UserService;
import com.zzx.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private OssUtil ossUtil;

    /**
     * 发布帖子接口
     * 处理用户发布新帖子的请求
     *
     * @param session HTTP会话对象
     * @param post 帖子对象，包含标题和内容
     * @param category 帖子板块
     * @param prize 奖励积分（问答板块使用）
     * @param coverImage 封面图片文件
     * @return 发布结果消息
     */
    @RequestMapping(value = "/sendPost.do", method = RequestMethod.POST)
    @ResponseBody
    public String sendPost(HttpSession session, Post post, String category, Integer prize,
                          @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        User user = (User)session.getAttribute("user");
        if (null != user) {
            // 检查用户是否被禁言
            if (user.getUstate() == 0)
                return "你已被禁言";

            // 检查是否允许互动（管理员不受限制）
            if (!HostController.allowInteraction && user.getLevel() != 0)
                return "管理员已关闭互动功能";

            // 预设板块列表
            List<String> validCategories = Arrays.asList("娱乐", "技术", "美食", "旅游", "问题");

            // 验证帖子标题、内容和板块
            if ((post.getPtitle().length() > 0 && post.getPtitle().length() <= 30) &&
                (post.getPbody().length() > 0 && post.getPbody().length() < 1000) &&
                (category != null && validCategories.contains(category))) {

                // 如果是问题板块，验证奖励积分
                if ("问题".equals(category)) {
                    if (prize == null || prize < 1 || prize > 10) {
                        return "问题板块必须设置奖励积分(1-10)";
                    }
                    // 检查用户积分是否足够
                    if (user.getScore() == null || user.getScore() < prize) {
                        return "积分不足，无法设置该奖励积分";
                    }
                } else {
                    prize = 0; // 非问题板块奖励为0
                }

                // 处理换行符，转换为HTML格式
                post.setPbody(post.getPbody().replaceAll("\n", "<br />"));
                post.setUser(user);
                post.setCategory(category);
                post.setPrize(prize);

                // 设置发帖时间和最后回复时间
                Date date = new Date();
                post.setPsendtime(date);
                post.setLastreplytime(date);
                // 设置默认非置顶状态
                post.setIsSticky(0);

                // 处理封面图片上传
                if (coverImage != null && !coverImage.isEmpty()) {
                    try {
                        // 验证图片格式
                        String originalFilename = coverImage.getOriginalFilename();
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                        if (!fileExtension.equals(".jpg") && !fileExtension.equals(".jpeg") && !fileExtension.equals(".png")) {
                            return "只支持JPG和PNG格式的图片";
                        }

                        String imageUrl = ossUtil.uploadFile(coverImage, "post_covers");
                        post.setPath(imageUrl);
                    } catch (Exception e) {
                        return "图片上传失败: " + e.getMessage();
                    }
                }

                postService.save(post);

                // 如果是问题板块，扣除用户积分
                if ("问题".equals(category) && prize > 0) {
                    boolean deductSuccess = userService.deductUserScore(user.getUid(), prize);
                    if (!deductSuccess) {
                        return "积分扣除失败，发帖失败";
                    }
                    // 更新session中的用户积分信息
                    User updatedUser = userService.findUserByUid(user.getUid());
                    session.setAttribute("user", updatedUser);
                }

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

            // 检查当前用户是否为帖子创建者
            boolean isPostCreator = post.getUser().getUid().equals(user.getUid());
            model.addAttribute("isPostCreator", isPostCreator);
        } else {
            post.setIsFavorited(false);
            model.addAttribute("isPostCreator", false);
        }

        model.addAttribute("post", post);
        model.addAttribute("videoRedirectEnabled", HostController.videoRedirectEnabled);

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

            // 检查是否允许互动（管理员不受限制）
            if (!HostController.allowInteraction && user.getLevel() != 0)
                return "管理员已关闭互动功能";

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

                // 发送通知给帖子作者（排除自己回复自己）
                Post repliedPost = postService.findPostByPid(reply.getPost().getPid().longValue());
                if (!repliedPost.getUser().getUid().equals(user.getUid())) {
                    String content = user.getUname() + " 回复了你的帖子《" + repliedPost.getPtitle() + "》";
                    notificationService.notify(repliedPost.getUser().getUid(), "reply", content,
                        user.getUid(), user.getUname(), repliedPost.getPid().intValue(), repliedPost.getPtitle());
                }

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
            if (user.getUstate() != 0) {
                // 如果帖子有封面图片，先删除OSS中的图片
                if (post.getPath() != null && !post.getPath().isEmpty()) {
                    try {
                        ossUtil.deleteFile(post.getPath());
                    } catch (Exception e) {
                        // 记录日志但继续删除帖子
                        System.err.println("删除OSS图片失败: " + e.getMessage());
                    }
                }
                postService.deletePost(pid);
            }
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
     * 查看指定用户的帖子
     * @param uid 用户ID
     * @param model Spring MVC模型对象
     * @return 用户帖子页面视图
     */
    @RequestMapping(value = "/user/{uid}/posts", method = RequestMethod.GET)
    public String userPosts(@PathVariable Integer uid, Model model) {
        User targetUser = userService.findUserByUid(uid);
        if (targetUser == null) {
            return "redirect:/";
        }
        List<Post> posts = postService.findPostsByUserId(uid.longValue());
        model.addAttribute("posts", posts);
        model.addAttribute("targetUser", targetUser);
        return "user_posts";
    }

    /**
     * 更新帖子内容接口
     * 允许用户修改自己帖子的内容（不能修改标题）
     *
     * @param post 帖子对象，包含新的内容
     * @param session HTTP会话对象
     * @param coverImage 新的封面图片文件（可选）
     * @return 更新结果消息
     */
    @RequestMapping(value = "/updatePostContent.do", method = RequestMethod.POST)
    @ResponseBody
    public String updatePostContent(Post post, HttpSession session,
                                   @RequestParam(value = "coverImage", required = false) MultipartFile coverImage) {
        User user = (User)session.getAttribute("user");
        if (user != null) {
            // 验证内容长度
            if (post.getPbody().length() > 0 && post.getPbody().length() < 1000) {
                // 处理换行符，转换为HTML格式
                post.setPbody(post.getPbody().replaceAll("\n", "<br />"));
                post.setUser(user);

                // 处理封面图片更新
                if (coverImage != null && !coverImage.isEmpty()) {
                    try {
                        // 验证图片格式
                        String originalFilename = coverImage.getOriginalFilename();
                        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                        if (!fileExtension.equals(".jpg") && !fileExtension.equals(".jpeg") && !fileExtension.equals(".png")) {
                            return "只支持JPG和PNG格式的图片";
                        }

                        // 先删除旧图片
                        Post existingPost = postService.findPostByPid(post.getPid());
                        if (existingPost.getPath() != null && !existingPost.getPath().isEmpty()) {
                            ossUtil.deleteFile(existingPost.getPath());
                        }
                        // 上传新图片
                        String imageUrl = ossUtil.uploadFile(coverImage, "post_covers");
                        post.setPath(imageUrl);
                    } catch (Exception e) {
                        return "图片上传失败: " + e.getMessage();
                    }
                }

                postService.updatePostContent(post);
                return "更新成功";
            } else {
                return "内容长度不符合要求";
            }
        }
        return "未登录";
    }

    /**
     * 批评扣分接口
     * 每次狠狠批评扣除用户10积分
     *
     * @param session HTTP会话对象
     * @return 扣分结果
     */
    @RequestMapping(value = "/critique/deduct", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> deductCritiquePoints(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User onlineUser = (User) session.getAttribute("user");
        if (onlineUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        boolean deducted = userService.deductUserScore(onlineUser.getUid(), 10);
        if (deducted) {
            User updatedUser = userService.findUserByUid(onlineUser.getUid());
            session.setAttribute("user", updatedUser);
            result.put("success", true);
            result.put("newScore", updatedUser.getScore());
        } else {
            result.put("success", false);
            result.put("message", "积分不足，无法批评");
        }
        return result;
    }

    /**
     * 狠狠认同接口
     * 每次认同回复给用户加50积分
     *
     * @param session HTTP会话对象
     * @return 加积分结果
     */
    @RequestMapping(value = "/agree/add", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addAgreePoints(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User onlineUser = (User) session.getAttribute("user");
        if (onlineUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        userService.addUserScore(onlineUser.getUid(), 10);
        User updatedUser = userService.findUserByUid(onlineUser.getUid());
        session.setAttribute("user", updatedUser);
        result.put("success", true);
        result.put("newScore", updatedUser.getScore());
        return result;
    }

    /**
     * 视频播放页面
     * 显示狠狠批评通关后的视频
     *
     * @param pid 帖子ID
     * @return 视频播放页面视图
     */
    @RequestMapping(value = "/video/{pid}", method = RequestMethod.GET)
    public String videoPage(@PathVariable Long pid) {
        return "video";
    }

    @RequestMapping(value = "/video/pay", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> payForVideo(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        boolean deducted = userService.deductUserScore(user.getUid(), 50);
        if (deducted) {
            User updatedUser = userService.findUserByUid(user.getUid());
            session.setAttribute("user", updatedUser);
            result.put("success", true);
            result.put("newScore", updatedUser.getScore());
        } else {
            result.put("success", false);
            result.put("message", "积分余额不够");
        }
        return result;
    }

    @RequestMapping(value = "/video/verify", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> verifyUser(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        // 如果未认证则认证，已认证不做操作
        if (user.getVerified() == null || user.getVerified() == 0) {
            userService.verifyUser(user.getUid());
            User updatedUser = userService.findUserByUid(user.getUid());
            session.setAttribute("user", updatedUser);
            result.put("success", true);
            result.put("message", "认证成功");
        } else {
            result.put("success", true);
            result.put("message", "已认证");
        }
        return result;
    }

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
