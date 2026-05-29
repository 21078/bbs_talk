package com.zzx.controller;

import com.zzx.exception.MessageException;
import com.zzx.model.Favorite;
import com.zzx.model.Page;
import com.zzx.model.Post;
import com.zzx.model.User;
import com.zzx.service.FavoriteService;
import com.zzx.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param page 页码参数，可选参数，默认为第一页
     * @param category 板块筛选参数，可选参数
     * @return 收藏列表页面
     */
    @GetMapping("/favorite/list")
    public String favoriteList(HttpSession session, Model model,
                               @RequestParam(value = "page", required = false) Long page,
                               @RequestParam(value = "category", required = false) String category) throws MessageException {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // 创建分页参数Map
        Map<String, Object> map = new HashMap<>();
        // 设置起始页，如果page为null则从第0页开始，否则从page-1开始
        long startPage = page == null ? 0 : page - 1;
        map.put("startPage", startPage);
        map.put("uid", user.getUid().longValue());

        // 调用服务层方法获取分页收藏帖子数据
        Page<Post> pageResult;
        if (category == null || category.isEmpty()) {
            // 没有板块筛选，获取所有收藏的帖子
            pageResult = favoriteService.findFavoritePostsByPage(map);
        } else {
            // 有板块筛选，获取指定板块的收藏帖子
            map.put("category", category);
            pageResult = favoriteService.findFavoritePostsByPageAndCategory(map);
        }

        // 将当前选择的板块传递到前端
        model.addAttribute("currentCategory", category);

        // 检查当前用户是否已收藏每个帖子（应该都是已收藏的，这里为了保持一致性）
        if (pageResult.getModelList() != null) {
            for (Post post : pageResult.getModelList()) {
                post.setIsFavorited(true);
            }
        }

        model.addAttribute("page", pageResult);
        return "favorite_list";
    }
}