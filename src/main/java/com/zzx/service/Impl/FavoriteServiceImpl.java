package com.zzx.service.Impl;

import com.zzx.mapper.FavoriteMapper;
import com.zzx.model.Favorite;
import com.zzx.model.Page;
import com.zzx.model.Post;
import com.zzx.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 收藏服务实现类
 * 实现收藏相关的业务逻辑
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    /**
     * 添加收藏
     * @param favorite 收藏对象
     */
    @Override
    public void save(Favorite favorite) {
        favorite.setFavtime(new Date());
        favoriteMapper.save(favorite);
    }

    /**
     * 取消收藏
     * @param uid 用户ID
     * @param pid 帖子ID
     */
    @Override
    public void delete(Integer uid, Long pid) {
        favoriteMapper.delete(uid, pid);
    }

    /**
     * 查询用户是否已收藏某帖子
     * @param uid 用户ID
     * @param pid 帖子ID
     * @return 收藏对象，如果未收藏则返回null
     */
    @Override
    public Favorite findByUserAndPost(Integer uid, Long pid) {
        return favoriteMapper.findByUserAndPost(uid, pid);
    }

    /**
     * 统计帖子的收藏数量
     * @param pid 帖子ID
     * @return 收藏数量
     */
    @Override
    public Long countByPostId(Long pid) {
        return favoriteMapper.countByPostId(pid);
    }

    /**
     * 查询用户的收藏列表
     * @param uid 用户ID
     * @return 收藏列表
     */
    @Override
    public List<Favorite> findByUserId(Integer uid) {
        return favoriteMapper.findByUserId(uid);
    }

    /**
     * 删除帖子的所有收藏记录
     * @param pid 帖子ID
     */
    @Override
    public void deleteByPostId(Long pid) {
        favoriteMapper.deleteByPostId(pid);
    }

    /**
     * 分页查询用户的收藏帖子
     * @param map 查询参数，包含uid和startPage
     * @return 分页的帖子结果
     */
    @Override
    public Page<Post> findFavoritePostsByPage(Map<String, Object> map) {
        Page<Post> page = new Page<>();

        // 设置当前页码
        Object startPageObj = map.get("startPage");
        Long startPage = startPageObj != null ? ((Number) startPageObj).longValue() : 0L;
        page.setCurrentPage(startPage != null ? (int)(startPage + 1) : 1);

        // 计算起始位置（乘以每页显示数量）
        map.put("startPage", startPage * page.getShowCount());

        // 查询帖子列表
        List<Post> posts = favoriteMapper.findFavoritePostsByPage(map);
        page.setModelList(posts != null ? posts : new ArrayList<>());

        // 查询总数
        Long total = favoriteMapper.countFavoritePosts(map);

        // 计算总页数
        int totalPages = total > 0 ? (int) Math.ceil((double) total / 4) : 1;
        page.setPageTotal(totalPages);

        return page;
    }

    /**
     * 分页查询用户的收藏帖子（按板块筛选）
     * @param map 查询参数，包含uid、startPage和category
     * @return 分页的帖子结果
     */
    @Override
    public Page<Post> findFavoritePostsByPageAndCategory(Map<String, Object> map) {
        Page<Post> page = new Page<>();

        // 设置当前页码
        Object startPageObj = map.get("startPage");
        Long startPage = startPageObj != null ? ((Number) startPageObj).longValue() : 0L;
        page.setCurrentPage(startPage != null ? (int)(startPage + 1) : 1);

        // 计算起始位置（乘以每页显示数量）
        map.put("startPage", startPage * page.getShowCount());

        // 查询帖子列表
        List<Post> posts = favoriteMapper.findFavoritePostsByPageAndCategory(map);
        page.setModelList(posts != null ? posts : new ArrayList<>());

        // 查询总数
        Long total = favoriteMapper.countFavoritePostsByCategory(map);

        // 计算总页数
        int totalPages = total > 0 ? (int) Math.ceil((double) total / 4) : 1;
        page.setPageTotal(totalPages);

        return page;
    }
}