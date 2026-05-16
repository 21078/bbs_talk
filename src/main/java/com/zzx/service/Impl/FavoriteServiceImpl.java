package com.zzx.service.Impl;

import com.zzx.mapper.FavoriteMapper;
import com.zzx.model.Favorite;
import com.zzx.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

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
}