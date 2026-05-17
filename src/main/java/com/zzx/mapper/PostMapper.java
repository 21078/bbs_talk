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

    /**
     * 根据板块分页查询帖子
     *
     * @param map 查询参数，包含分页和板块信息
     * @return 帖子列表
     */
    List<Post> findPostByPageAndCategory(Map<String, Object> map);

    /**
     * 根据板块查询帖子数量
     *
     * @param category 板块名称
     * @return 帖子数量
     */
    Integer getPostCountByCategory(String category);

    /**
     * 切换帖子置顶状态
     *
     * @param pid 帖子ID
     * @param isSticky 置顶状态：0取消置顶，1置顶
     */
    void toggleSticky(Long pid, Integer isSticky);
}
