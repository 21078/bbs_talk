package com.zzx.mapper;

import com.zzx.model.Post;

import java.util.List;
import java.util.Map;

public interface PostMapper {


    /**
     * 保存帖子
     *
     * @param post
     * @return
     */
    int save(Post post);


    /**
     * 查询所有帖子
     *
     * @return
     */
    List<Post> findAllPost();

    /**
     * 根据帖子id查询帖子
     *
     * @param pid
     * @return
     */
    Post findPostById(Long pid);

    /**
     * 更新帖子回复时间
     */
    void updatePostLastReplyTime(Post post);


    /**
     * 根据帖子id 删除帖子
     *
     * @param pid
     */
    void deletePost(Long pid);


    /**
     * 分页查询帖子
     *
     * @param map
     * @return
     */
    List<Post> findPostByPage(Map<String, Long> map);


    /**
     * 查询帖子数量
     *
     * @return
     */
    Integer getPostCount();

    /**
     * 根据用户ID查询帖子
     *
     * @param uid 用户ID
     * @return 帖子列表
     */
    List<Post> findPostsByUserId(Long uid);

    /**
     * 更新帖子内容
     *
     * @param post 帖子对象
     */
    void updatePostContent(Post post);
}
