package com.zzx.service;

import com.zzx.model.Favorite;
import com.zzx.model.Page;
import com.zzx.model.Post;
import java.util.List;
import java.util.Map;

/**
 * 收藏服务接口
 * 定义收藏相关的业务操作
 */
public interface FavoriteService {

    /**
     * 添加收藏
     * @param favorite 收藏对象
     */
    void save(Favorite favorite);

    /**
     * 取消收藏
     * @param uid 用户ID
     * @param pid 帖子ID
     */
    void delete(Integer uid, Long pid);

    /**
     * 查询用户是否已收藏某帖子
     * @param uid 用户ID
     * @param pid 帖子ID
     * @return 收藏对象，如果未收藏则返回null
     */
    Favorite findByUserAndPost(Integer uid, Long pid);

    /**
     * 统计帖子的收藏数量
     * @param pid 帖子ID
     * @return 收藏数量
     */
    Long countByPostId(Long pid);

    /**
     * 查询用户的收藏列表
     * @param uid 用户ID
     * @return 收藏列表
     */
    List<Favorite> findByUserId(Integer uid);

    /**
     * 删除帖子的所有收藏记录
     * @param pid 帖子ID
     */
    void deleteByPostId(Long pid);

    /**
     * 分页查询用户的收藏帖子
     * @param map 查询参数，包含uid和startPage
     * @return 分页的帖子结果
     */
    Page<Post> findFavoritePostsByPage(Map<String, Object> map);

    /**
     * 分页查询用户的收藏帖子（按板块筛选）
     * @param map 查询参数，包含uid、startPage和category
     * @return 分页的帖子结果
     */
    Page<Post> findFavoritePostsByPageAndCategory(Map<String, Object> map);
}