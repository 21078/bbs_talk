package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
