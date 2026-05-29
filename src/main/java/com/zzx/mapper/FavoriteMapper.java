package com.zzx.mapper;

import com.zzx.model.Favorite;
import com.zzx.model.Post;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

/**
 * 收藏数据访问接口
 * 处理收藏相关的数据库操作
 */
@Mapper
public interface FavoriteMapper {

    /**
     * 添加收藏
     * @param favorite 收藏对象
     */
    @Insert("INSERT INTO favorite(uid, pid, favtime) VALUES(#{uid}, #{pid}, #{favtime})")
    void save(Favorite favorite);

    /**
     * 取消收藏
     * @param uid 用户ID
     * @param pid 帖子ID
     */
    @Delete("DELETE FROM favorite WHERE uid = #{uid} AND pid = #{pid}")
    void delete(@Param("uid") Integer uid, @Param("pid") Long pid);

    /**
     * 查询用户是否已收藏某帖子
     * @param uid 用户ID
     * @param pid 帖子ID
     * @return 收藏对象，如果未收藏则返回null
     */
    @Select("SELECT * FROM favorite WHERE uid = #{uid} AND pid = #{pid}")
    Favorite findByUserAndPost(@Param("uid") Integer uid, @Param("pid") Long pid);

    /**
     * 统计帖子的收藏数量
     * @param pid 帖子ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM favorite WHERE pid = #{pid}")
    Long countByPostId(Long pid);

    /**
     * 查询用户的收藏列表
     * @param uid 用户ID
     * @return 收藏列表
     */
    @Select("SELECT * FROM favorite WHERE uid = #{uid} ORDER BY favtime DESC")
    List<Favorite> findByUserId(Integer uid);

    /**
     * 删除帖子的所有收藏记录
     * @param pid 帖子ID
     */
    @Delete("DELETE FROM favorite WHERE pid = #{pid}")
    void deleteByPostId(Long pid);

    /**
     * 分页查询用户的收藏帖子
     * @param map 查询参数，包含uid和startPage
     * @return 帖子列表
     */
    List<Post> findFavoritePostsByPage(Map<String, Object> map);

    /**
     * 分页查询用户的收藏帖子（按板块筛选）
     * @param map 查询参数，包含uid、startPage和category
     * @return 帖子列表
     */
    List<Post> findFavoritePostsByPageAndCategory(Map<String, Object> map);

    /**
     * 统计用户收藏的帖子总数
     * @param map 查询参数，包含uid
     * @return 总数
     */
    Long countFavoritePosts(Map<String, Object> map);

    /**
     * 统计用户收藏的帖子总数（按板块筛选）
     * @param map 查询参数，包含uid和category
     * @return 总数
     */
    Long countFavoritePostsByCategory(Map<String, Object> map);
}