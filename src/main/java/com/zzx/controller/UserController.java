package com.zzx.controller;

import com.zzx.exception.MessageException;
import com.zzx.model.User;
import com.zzx.service.UserService;
import com.zzx.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户相关的HTTP请求，包括注册、登录、退出、权限管理等
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OssUtil ossUtil;

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
        // 检查是否允许注册
        if (!HostController.allowRegistration) {
            return "注册失败:管理员已关闭注册功能";
        }

        // 验证用户名和密码长度
        if (user.getUname().length() > 16 || user.getUpwd().length() > 16 || user.getUpwd().length() < 6) {
            return "注册失败:用户名或密码长度必须小于16位";
        }

        // 设置用户默认属性
        user.setLevel(1); // 设置为普通用户
        user.setUcreatetime(new Date()); // 设置创建时间
        user.setUstate(1); // 设置用户状态为正常
        user.setScore(100);
        user.setPath("https://bbspicturebed.oss-cn-huhehaote.aliyuncs.com/post_covers/default_face.jpg"); // 设置默认头像

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
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 个人信息页面视图
     */
    @RequestMapping("/person.do")
    public String user(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/"; // 未登录则重定向到首页

        // 从数据库获取最新的用户信息，确保显示的数据是最新的
        User latestUser = userService.findUserByUid(user.getUid());
        if (latestUser != null) {
            // 将最新的用户信息添加到模型中，而不是更新session
            model.addAttribute("currentUser", latestUser);
        } else {
            // 如果数据库查询失败，使用session中的信息作为后备
            model.addAttribute("currentUser", user);
        }

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
     * 修改用户的联系方式、职业等信息，支持头像上传
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @param phone 联系电话
     * @param career 职业
     * @param address 工作地址
     * @param avatarImage 头像图片文件
     * @return 操作结果页面
     */
    @PostMapping("/updateProfile.do")
    public String updateProfile(Model model, HttpSession session,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String career,
                                @RequestParam(required = false) String address,
                                @RequestParam(value = "avatarImage", required = false) MultipartFile avatarImage) {

        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";

        // 处理头像上传
        if (avatarImage != null && !avatarImage.isEmpty()) {
            try {
                // 验证图片格式
                String originalFilename = avatarImage.getOriginalFilename();
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
                if (!fileExtension.equals(".jpg") && !fileExtension.equals(".jpeg") && !fileExtension.equals(".png")) {
                    model.addAttribute("message", "只支持JPG和PNG格式的图片");
                    return "error";
                }

                // 验证文件大小 (5MB)
                if (avatarImage.getSize() > 5 * 1024 * 1024) {
                    model.addAttribute("message", "头像图片大小不能超过5MB");
                    return "error";
                }

                // 上传新头像
                String imageUrl = ossUtil.uploadFile(avatarImage, "user_avatars");

                // 删除旧头像（如果不是默认头像）
                if (user.getPath() != null && !user.getPath().contains("default_face.jpg")) {
                    try {
                        ossUtil.deleteFile(user.getPath());
                    } catch (Exception e) {
                        // 记录日志但继续处理
                        System.err.println("删除旧头像失败: " + e.getMessage());
                    }
                }

                user.setPath(imageUrl);
            } catch (Exception e) {
                model.addAttribute("message", "头像上传失败: " + e.getMessage());
                return "error";
            }
        }

        // 验证联系电话是否为全数字
        if (phone != null && !phone.isEmpty() && !phone.matches("\\d+")) {
            model.addAttribute("message", "联系电话必须为数字");
            return "error";
        }

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

    /**
     * 注销用户账户接口
     * 删除用户及其所有相关数据（帖子、回复），然后重定向到首页
     * 只有普通用户可以注销账户，管理员不能注销
     *
     * @param model Spring MVC模型对象
     * @param session HTTP会话对象
     * @return 操作结果页面
     */
    /**
     * 获取用户个人信息接口
     * 返回指定用户的公开信息（头像、电话、工作地址）
     *
     * @param uid 用户ID
     * @return 用户信息JSON
     */
    @ResponseBody
    @RequestMapping(value = "/user/profile/{uid}", method = RequestMethod.GET)
    public Map<String, Object> getUserProfile(@PathVariable Integer uid) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userService.findUserByUid(uid);
            if (user != null) {
                result.put("uid", user.getUid());
                result.put("uname", user.getUname());
                result.put("path", user.getPath() != null ? user.getPath() : "");
                result.put("phone", user.getPhone() != null ? user.getPhone() : "未设置");
                result.put("career", user.getCareer() != null ? user.getCareer() : "未设置");
                result.put("address", user.getAddress() != null ? user.getAddress() : "未设置");
                result.put("verified", user.getVerified() != null ? user.getVerified() : 0);
            } else {
                result.put("error", "用户不存在");
            }
        } catch (Exception e) {
            result.put("error", "获取用户信息失败");
        }
        return result;
    }

    @PostMapping("/deleteAccount.do")
    public String deleteAccount(Model model, HttpSession session) {
        User user = (User)session.getAttribute("user");
        if (user == null)
            return "redirect:/";

        // 检查是否为管理员，管理员不能注销
        if (user.getLevel() == 0) {
            model.addAttribute("message", "管理员账户不能注销");
            return "error";
        }

        try {
            // 删除用户及其所有相关数据
            userService.deleteUser(user.getUid());

            // 清除session
            session.removeAttribute("user");

            // 注销成功，重定向到首页
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("message", "注销失败：" + e.getMessage());
            return "error";
        }
    }

}
