package com.zzx.service;

import com.zzx.model.Page;
import com.zzx.model.Post;

import java.util.List;
import java.util.Map;

public interface PostService {


    /**
     * 保存帖子
     * @param post 帖子对象
     * @return void
     */
    void save(Post post);

    /**
     * 查询所有帖子
     * @return List<Post> 帖子列表
     */
    List<Post> findAllPost();

    /**
     * 根据帖子id查询帖子
     * @param pid 帖子ID
     * @return Post 帖子对象
     */
    Post findPostByPid(Long pid);


    /**
     * 根据帖子id删除帖子
     * @param pid 帖子ID
     * @return void
     */
    void deletePost(Long pid);


    /**
     * 分页查询帖子
     * @param map 分页参数
     * @return Page<Post> 分页对象
     */
    Page<Post> findPostByPage(Map<String, Long> map);

    /**
     * 根据用户ID查询帖子
     * @param uid 用户ID
     * @return List<Post> 帖子列表
     */
    List<Post> findPostsByUserId(Long uid);

    /**
     * 更新帖子内容
     * @param post 帖子对象
     * @return void
     */
    void updatePostContent(Post post);
}
