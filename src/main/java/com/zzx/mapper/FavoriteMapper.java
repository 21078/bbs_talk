package com.zzx.mapper;

import com.zzx.model.Favorite;
import org.apache.ibatis.annotations.*;
import java.util.List;

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
}