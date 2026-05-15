package com.zzx.controller;

import com.zzx.exception.MessageException;
import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括注册、登录、退出、权限管理等
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册接口
     * 处理新用户注册请求
     *
     * @param user 用户对象，包含注册信息
     * @param session HTTP会话对象
     * @return 注册结果消息
     */
    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public String register(User user, HttpSession session) {
        // 验证用户名和密码长度
        if (user.getUname().length() > 16 || user.getUpwd().length() > 16 || user.getUpwd().length() < 6) {
            return "注册失败:用户名或密码长度必须小于16位";
        }

        // 设置用户默认属性
        user.setLevel(1); // 设置为普通用户
        user.setUcreatetime(new Date()); // 设置创建时间
        user.setUstate(1); // 设置用户状态为正常

        try {
            userService.register(user);
            return "注册成功";
        } catch (MessageException e) {
            return e.getMessage();
        }
    }

    /**
     * 用户登录接口
     * 处理用户登录请求
     *
     * @param user 用户对象，包含登录信息
     * @param autoFlag 自动登录标识
     * @param session HTTP会话对象
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @return 登录结果消息
     */
    @ResponseBody
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public String login(User user,
                        @RequestParam(value = "autoLogin", required = false) String autoFlag, HttpSession session,
                        HttpServletRequest request, HttpServletResponse response) {

        // 调用用户服务进行登录验证
        user = userService.login(user);
        if (null != user) {
            // 登录成功，将用户信息存入session
            session.setAttribute("user", user);
            // 创建会话cookie，设置30分钟有效期
            Cookie c = new Cookie("JSESSIONID", session.getId());
            c.setMaxAge(60 * 30);
            response.addCookie(c);
            return "登录成功";
        } else
            return "登录失败";
    }

    /**
     * 用户退出接口
     * 清除用户会话信息
     *
     * @param session HTTP会话对象
     * @return 退出结果消息
     */
    @ResponseBody
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    public String loginout(HttpSession session) {
        session.removeAttribute("user");
        return "退出成功";
    }

    /**
     * 禁言用户接口（管理员权限）
     * 禁止指定用户发言
     *
     * @param uid 用户ID
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @RequestMapping(value = "/ban/{uid}")
    @ResponseBody
    public String banUser(@PathVariable Integer uid, HttpSession session) {
        // 检查当前用户权限
        User onlineUser = (User)session.getAttribute("user");
        if (onlineUser == null || onlineUser.getLevel() == 1)
            return "没有权限";

        // 获取目标用户信息
        User user = userService.findUserByUid(uid);
        if (user.getLevel() == 0)
            return "此账号为管理员";

        // 执行禁言操作
        userService.banUser(user);
        return "禁言成功";
    }

    /**
     * 解禁用户接口（管理员权限）
     * 解除指定用户的禁言状态
     *
     * @param uid 用户ID
     * @param session HTTP会话对象
     * @return 操作结果消息
     */
    @RequestMapping(value = "/unban/{uid}")
    @ResponseBody
    public String unbanUser(@PathVariable Integer uid, HttpSession session) {
        // 检查当前用户权限
        User onlineUser = (User)session.getAttribute("user");
        if (onlineUser == null || onlineUser.getLevel() == 1)
            return "没有权限";

        User user = new User();
        user.setUid(uid);
        userService.unbanUser(user);
        return "解禁成功";
    }

    /**
     * 用户个人信息页面
     * 显示当前登录用户的个人信息
     *
     * @param session HTTP会话对象
     * @return 个人信息页面视图
     */
    @RequestMapping("/person.do")
    public String user(HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/"; // 未登录则重定向到首页
        return "person";
    }

    /**
     * 更新用户密码接口
     * 修改当前用户的登录密码
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @param oldPwd 原密码
     * @param newPwd 新密码
     * @return 操作结果页面
     */
    @PostMapping("/updatePassword.do")
    public String updatePassword(Model model, HttpSession session, @RequestParam String oldPwd,
                                 @RequestParam String newPwd) {

        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";

        // 验证新密码长度
        if (newPwd.length() <= 6 || newPwd.length() > 16) {
            model.addAttribute("message", "新密码长度(6,16]位");
            return "error";
        }

        try {
            userService.updatePassword(user.getUname(), oldPwd, newPwd);
            session.removeAttribute("user"); // 修改密码后需要重新登录
        } catch (MessageException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
        return "redirect:/";

    }

    /**
     * 更新用户个人信息接口
     * 修改用户的联系方式、职业等信息
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @param phone 联系电话
     * @param career 职业
     * @param address 工作地址
     * @return 操作结果页面
     */
    @PostMapping("/updateProfile.do")
    public String updateProfile(Model model, HttpSession session,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String career,
                                @RequestParam(required = false) String address) {

        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";

        // 更新用户信息
        user.setPhone(phone);
        user.setCareer(career);
        user.setAddress(address);

        try {
            userService.updateUser(user);
            // 更新session中的用户信息
            session.setAttribute("user", user);
        } catch (Exception e) {
            model.addAttribute("message", "更新失败");
            return "error";
        }
        return "redirect:/person.do";
    }

}
