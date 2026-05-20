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

    /**
     * 根据板块分页查询帖子
     * @param map 分页参数
     * @param category 板块名称
     * @return Page<Post> 分页对象
     */
    Page<Post> findPostByPageAndCategory(Map<String, Long> map, String category);

    /**
     * 切换帖子置顶状态
     * @param pid 帖子ID
     * @param isSticky 置顶状态：0取消置顶，1置顶
     */
    void toggleSticky(Long pid, Integer isSticky);

    /**
     * 根据用户ID删除该用户的所有帖子
     * @param uid 用户ID
     */
    void deletePostsByUserId(Long uid);
}
