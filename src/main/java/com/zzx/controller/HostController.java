package com.zzx.controller;

import com.zzx.model.User;
import com.zzx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HostController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/host.do")
    public String host(Model model, HttpSession session) {

        User user = (User)session.getAttribute("user");
        if (null != user && user.getLevel() == 0) {

            //查询所有用户
            model.addAttribute("userList", userService.findAllUser());

            return "host";
        }
        return "redirect:/";
    }
}
