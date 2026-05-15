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

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping(value = "/register.do", method = RequestMethod.POST)
    public String register(User user, HttpSession session) {
        if (user.getUname().length() > 16 || user.getUpwd().length() > 16 || user.getUpwd().length() < 6) {
            return "注册失败:用户名或密码长度必须小于16位";
        }

        // 直接进行注册流程
        // 密码明文存储，无需加密
        user.setLevel(1);
        user.setUcreatetime(new Date());
        user.setUstate(1);
        try {
            userService.register(user);
            return "注册成功";
        } catch (MessageException e) {
            return e.getMessage();
        }
    }

    @ResponseBody
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public String login(User user,
                        @RequestParam(value = "autoLogin", required = false) String autoFlag, HttpSession session,
                        HttpServletRequest request, HttpServletResponse response) {

        // 直接进行登录流程
        // 密码明文存储，无需加密

        user = userService.login(user);
        if (null != user) {
            session.setAttribute("user", user);
            Cookie c = new Cookie("JSESSIONID", session.getId());
            // session默认销毁时间30分钟
            c.setMaxAge(60 * 30);
            response.addCookie(c);
            return "登录成功";
        } else
            return "登录失败";
    }

    @ResponseBody
    @RequestMapping(value = "logout.do", method = RequestMethod.GET)
    public String loginout(HttpSession session) {
        session.removeAttribute("user");
        return "退出成功";
    }


    @RequestMapping(value = "/ban/{uid}")
    @ResponseBody
    public String banUser(@PathVariable Integer uid, HttpSession session) {

        User onlineUser = (User)session.getAttribute("user");
        if (onlineUser == null || onlineUser.getLevel() == 1)
            return "没有权限";
        User user = userService.findUserByUid(uid);
        if (user.getLevel() == 0)
            return "此账号为管理员";
        userService.banUser(user);
        return "禁言成功";
    }

    @RequestMapping(value = "/unban/{uid}")
    @ResponseBody
    public String unbanUser(@PathVariable Integer uid, HttpSession session) {

        User onlineUser = (User)session.getAttribute("user");
        if (onlineUser == null || onlineUser.getLevel() == 1)
            return "没有权限";
        User user = new User();
        user.setUid(uid);
        userService.unbanUser(user);
        return "解禁成功";
    }

    @RequestMapping("/person.do")
    public String user(HttpSession session) {

        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";
        return "person";
    }

    @PostMapping("/updatePassword.do")
    public String updatePassword(Model model, HttpSession session, @RequestParam String oldPwd,
                                 @RequestParam String newPwd) {

        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";
        if (newPwd.length() <= 6 || newPwd.length() > 16) {
            model.addAttribute("message", "新密码长度(6,16]位");
            return "error";
        }
        try {
            userService.updatePassword(user.getUname(), oldPwd, newPwd);
            session.removeAttribute("user");
        } catch (MessageException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
        return "redirect:/";

    }

}
