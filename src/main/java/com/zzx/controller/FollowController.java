package com.zzx.controller;

import com.zzx.exception.MessageException;
import com.zzx.model.Follow;
import com.zzx.model.Page;
import com.zzx.model.User;
import com.zzx.service.FollowService;
import com.zzx.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/follow/add/{followUid}")
    @ResponseBody
    public String addFollow(@PathVariable Integer followUid, HttpSession session) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        if (user.getUid().equals(followUid)) {
            return "不能关注自己";
        }

        Follow existing = followService.findByUserAndFollowUser(user.getUid(), followUid);
        if (existing != null) {
            return "已关注该用户";
        }

        Follow follow = new Follow();
        follow.setUid(user.getUid());
        follow.setFollowUid(followUid);
        followService.save(follow);

        // 发送通知给被关注者
        String content = user.getUname() + " 关注了你";
        notificationService.notify(followUid, "follow", content,
            user.getUid(), user.getUname(), null, null);

        return "关注成功";
    }

    @GetMapping("/follow/remove/{followUid}")
    @ResponseBody
    public String removeFollow(@PathVariable Integer followUid, HttpSession session) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        followService.delete(user.getUid(), followUid);
        return "取消关注成功";
    }

    @GetMapping("/follow/check/{followUid}")
    @ResponseBody
    public Map<String, Object> checkFollow(@PathVariable Integer followUid, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            result.put("isFollowed", false);
            result.put("loggedIn", false);
            return result;
        }

        Follow existing = followService.findByUserAndFollowUser(user.getUid(), followUid);
        result.put("isFollowed", existing != null);
        result.put("loggedIn", true);
        return result;
    }

    @GetMapping("/follow/list")
    public String followList(HttpSession session, Model model,
                              @RequestParam(value = "page", required = false) Long page,
                              @RequestParam(value = "type", defaultValue = "following") String type) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Map<String, Object> map = new HashMap<>();
        long startPage = page == null ? 0 : page - 1;
        map.put("startPage", startPage);
        map.put("uid", user.getUid());

        if ("fans".equals(type)) {
            Page<User> pageResult = followService.findFansUsersByPage(map);
            Map<Integer, Boolean> followStatusMap = new HashMap<>();
            for (User fan : pageResult.getModelList()) {
                Follow existing = followService.findByUserAndFollowUser(user.getUid(), fan.getUid());
                followStatusMap.put(fan.getUid(), existing != null);
            }
            model.addAttribute("followStatusMap", followStatusMap);
            model.addAttribute("page", pageResult);
        } else {
            Page<User> pageResult = followService.findFollowUsersByPage(map);
            model.addAttribute("page", pageResult);
        }

        model.addAttribute("currentType", type);
        return "follow_list";
    }
}
