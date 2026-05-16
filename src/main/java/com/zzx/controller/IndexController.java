package com.zzx.controller;


import com.zzx.model.Favorite;
import com.zzx.model.Page;
import com.zzx.model.Post;
import com.zzx.model.User;
import com.zzx.service.FavoriteService;
import com.zzx.service.PostService;
import com.zzx.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpSession;
import java.util.HashMap;

import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 首页控制器方法
     * 显示帖子列表，支持分页功能
     *
     * @param session HTTP会话对象
     * @param model Spring MVC模型对象，用于向视图传递数据
     * @param page 页码参数，可选参数，默认为第一页
     * @return 返回首页视图名称
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpSession session, Model model, @RequestParam(value = "page", required = false) Long page) {
        // 创建分页参数Map
        Map<String, Long> map = new HashMap<>();
        // 设置起始页，如果page为null则从第0页开始，否则从page-1页开始
        map.put("startPage", page == null ? 0 : page - 1);
        // 调用服务层方法获取分页帖子数据
        Page<Post> pageResult = postService.findPostByPage(map);

        // 检查当前用户是否已收藏每个帖子
        User user = (User) session.getAttribute("user");
        if (user != null && pageResult.getModelList() != null) {
            for (Post post : pageResult.getModelList()) {
                Favorite existing = favoriteService.findByUserAndPost(user.getUid(), post.getPid());
                post.setIsFavorited(existing != null);
            }
        }

        model.addAttribute("page", pageResult);

        return "index";
    }


}
