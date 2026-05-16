package com.zzx.controller;

import com.zzx.exception.MessageException;
import com.zzx.model.Favorite;
import com.zzx.model.Post;
import com.zzx.model.User;
import com.zzx.service.FavoriteService;
import com.zzx.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 收藏控制器
 * 处理收藏相关的HTTP请求
 */
@Controller
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private PostService postService;

    /**
     * 添加收藏
     * @param pid 帖子ID
     * @param session HTTP会话
     * @return JSON响应
     * @throws MessageException 当用户未登录时抛出异常
     */
    @GetMapping("/favorite/add/{pid}")
    @ResponseBody
    public String addFavorite(@PathVariable Long pid, HttpSession session) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        // 检查是否已收藏
        Favorite existing = favoriteService.findByUserAndPost(user.getUid(), pid);
        if (existing != null) {
            return "已收藏过该帖子";
        }

        Favorite favorite = new Favorite();
        favorite.setUid(user.getUid());
        favorite.setPid(pid);
        favoriteService.save(favorite);

        return "添加收藏成功";
    }

    /**
     * 取消收藏
     * @param pid 帖子ID
     * @param session HTTP会话
     * @return JSON响应
     * @throws MessageException 当用户未登录时抛出异常
     */
    @GetMapping("/favorite/remove/{pid}")
    @ResponseBody
    public String removeFavorite(@PathVariable Long pid, HttpSession session) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "请先登录";
        }

        favoriteService.delete(user.getUid(), pid);
        return "取消收藏成功";
    }

    /**
     * 查看用户的收藏列表
     * @param session HTTP会话
     * @param model 模型对象
     * @return 收藏列表页面
     */
    @GetMapping("/favorite/list")
    public String favoriteList(HttpSession session, Model model) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Favorite> favoriteList = favoriteService.findByUserId(user.getUid());

        // 为每个收藏添加帖子详情
        for (Favorite favorite : favoriteList) {
            Post post = postService.findPostByPid(favorite.getPid());
            favorite.setPost(post);
        }

        model.addAttribute("favoriteList", favoriteList);
        return "favorite_list";
    }
}