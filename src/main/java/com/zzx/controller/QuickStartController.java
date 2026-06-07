package com.zzx.controller;

import com.zzx.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

@Controller
public class QuickStartController {

    @RequestMapping(value = "/quick_start", method = RequestMethod.GET)
    public String quickStart(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && user.getVerified() != null && user.getVerified() == 1) {
            return "quick_start";
        }
        return "redirect:/";
    }
}
