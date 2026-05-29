package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * 主持人/管理员控制器
 * 处理管理员相关的HTTP请求，提供用户管理功能
 */
@Controller
public class HostController {

    @Autowired
    private UserService userService;

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
}
