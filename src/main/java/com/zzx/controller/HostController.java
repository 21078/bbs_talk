package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.NotificationService;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 主持人/管理员控制器
 * 处理管理员相关的HTTP请求，提供用户管理功能
 */
@Controller
public class HostController {

    /**
     * 全局变量：控制帖子12怒气值满后是否跳转视频
     * true - 跳转视频；false - 返回帖子详情页
     */
    public static boolean videoRedirectEnabled = false;

    /**
     * 全局变量：是否允许新用户注册
     * true - 允许注册；false - 关闭注册
     */
    public static boolean allowRegistration = true;

    /**
     * 全局变量：是否允许用户互动（发帖、回复）
     * true - 允许互动；false - 禁止互动（所有非管理员用户被禁言）
     */
    public static boolean allowInteraction = true;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationService notificationService;

    /**
     * 管理员后台页面
     * 显示所有用户信息，供管理员进行管理操作
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 管理员后台页面视图
     */
    @RequestMapping(value = "/host.do")
    public String host(Model model, HttpSession session) {

        User user = (User)session.getAttribute("user");
        // 检查用户是否为管理员
        if (null != user && user.getLevel() == 0) {

            // 查询所有用户信息
            model.addAttribute("userList", userService.findAllUser());
            model.addAttribute("videoRedirectEnabled", videoRedirectEnabled);
            model.addAttribute("allowRegistration", allowRegistration);
            model.addAttribute("allowInteraction", allowInteraction);

            return "host";
        }
        // 非管理员用户重定向到首页
        return "redirect:/";
    }

    /**
     * 管理员注销指定用户账户
     * 管理员可以注销普通用户账户
     *
     * @param uid 要注销的用户ID
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @ResponseBody
    @PostMapping("/admin/deleteUser/{uid}")
    public String adminDeleteUser(@PathVariable Integer uid, HttpSession session) {
        User currentUser = (User)session.getAttribute("user");
        if (currentUser == null || currentUser.getLevel() != 0) {
            return "没有权限执行此操作";
        }

        // 不能注销自己
        if (uid.equals(currentUser.getUid())) {
            return "不能注销自己的管理员账户";
        }

        try {
            userService.deleteUser(uid);
            return "用户注销成功";
        } catch (Exception e) {
            return "注销失败：" + e.getMessage();
        }
    }

    /**
     * 切换帖子12怒气值满后的跳转模式
     * 在视频跳转和返回帖子详情页之间切换
     *
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @ResponseBody
    @PostMapping("/admin/toggleVerified/{uid}")
    public String toggleVerified(@PathVariable Integer uid, HttpSession session) {
        User currentUser = (User)session.getAttribute("user");
        if (currentUser == null || currentUser.getLevel() != 0) {
            return "没有权限执行此操作";
        }
        // 允许管理员切换自己的认证状态
        userService.toggleVerified(uid);
        // 如果是切换自己的状态，更新session
        if (uid.equals(currentUser.getUid())) {
            User updatedUser = userService.findUserByUid(uid);
            session.setAttribute("user", updatedUser);
        }
        return "切换认证状态成功";
    }

    @ResponseBody
    @PostMapping("/admin/toggleVideoRedirect")
    public String toggleVideoRedirect(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getLevel() != 0) {
            return "没有权限执行此操作";
        }
        videoRedirectEnabled = !videoRedirectEnabled;
        return videoRedirectEnabled ? "视频跳转已开启" : "视频跳转已关闭";
    }

    @ResponseBody
    @PostMapping("/admin/toggleRegistration")
    public String toggleRegistration(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getLevel() != 0) {
            return "没有权限执行此操作";
        }
        allowRegistration = !allowRegistration;
        return allowRegistration ? "注册已开启" : "注册已关闭";
    }

    @ResponseBody
    @PostMapping("/admin/sendGlobalNotification")
    public String sendGlobalNotification(@RequestParam String content, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getLevel() != 0) {
            return "没有权限执行此操作";
        }
        if (content == null || content.trim().isEmpty()) {
            return "通知内容不能为空";
        }
        try {
            notificationService.notifyAllUsers("system", content.trim(), user.getUid(), user.getUname());
            return "全局通知已发送";
        } catch (Exception e) {
            return "发送失败：" + e.getMessage();
        }
    }

    @ResponseBody
    @PostMapping("/admin/toggleInteraction")
    public String toggleInteraction(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getLevel() != 0) {
            return "没有权限执行此操作";
        }
        allowInteraction = !allowInteraction;
        if (allowInteraction) {
            userService.unbanAllNonAdminUsers();
            return "互动已开启，已解禁所有用户";
        } else {
            userService.banAllNonAdminUsers();
            return "互动已关闭，已禁言所有非管理员用户";
        }
    }
}
