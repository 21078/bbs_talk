package com.zzx.controller;

import com.zzx.model.Notification;
import com.zzx.model.Page;
import com.zzx.model.User;
import com.zzx.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/page")
    public String notificationPage(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "all") String type,
                                   HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/";
        Page<Notification> pageResult = notificationService.getList(user.getUid(), page, type);
        model.addAttribute("page", pageResult);
        model.addAttribute("currentType", type);
        model.addAttribute("unreadTotal", notificationService.getUnreadCount(user.getUid()));
        model.addAttribute("unreadByType", notificationService.getUnreadCountByTypeMap(user.getUid()));
        return "notification_list";
    }

    @GetMapping("/notification/list")
    @ResponseBody
    public Map<String, Object> list(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "all") String type,
                                    HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) { result.put("success", false); return result; }
        result.put("success", true);
        result.put("page", notificationService.getList(user.getUid(), page, type));
        return result;
    }

    @GetMapping("/notification/unread-count")
    @ResponseBody
    public Map<String, Object> unreadCount(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) { result.put("count", 0); return result; }
        result.put("count", notificationService.getUnreadCount(user.getUid()));
        return result;
    }

    @GetMapping("/notification/unread-count-by-type")
    @ResponseBody
    public Map<String, Object> unreadCountByType(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) { result.put("success", false); return result; }
        result.put("success", true);
        result.put("counts", notificationService.getUnreadCountByTypeMap(user.getUid()));
        return result;
    }

    @PostMapping("/notification/read/{nid}")
    @ResponseBody
    public Map<String, Object> markRead(@PathVariable int nid) {
        Map<String, Object> result = new HashMap<>();
        notificationService.markRead(nid);
        result.put("success", true);
        return result;
    }

    @PostMapping("/notification/read-all")
    @ResponseBody
    public Map<String, Object> markAllRead(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        User user = (User) session.getAttribute("user");
        if (user == null) { result.put("success", false); return result; }
        notificationService.markAllRead(user.getUid());
        result.put("success", true);
        return result;
    }

    @PostMapping("/notification/delete/{nid}")
    @ResponseBody
    public Map<String, Object> delete(@PathVariable int nid) {
        Map<String, Object> result = new HashMap<>();
        notificationService.delete(nid);
        result.put("success", true);
        return result;
    }
}
