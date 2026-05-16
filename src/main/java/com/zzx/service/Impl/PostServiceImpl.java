package com.zzx.service.Impl;

import com.zzx.mapper.PostMapper;
import com.zzx.mapper.ReplyMapper;
import com.zzx.model.Page;
import com.zzx.model.Post;
import com.zzx.service.PostService;
import com.zzx.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 帖子服务实现类
 * 实现帖子相关的业务逻辑，包括发帖、查询、删除等
 */
@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private FavoriteService favoriteService;

    /**
     * 保存帖子
     * @param post 帖子对象
     * @return void
     */
    @Override
    public void save(Post post) {
        postMapper.save(post);
    }

    /**
     * 查询所有帖子
     * 获取所有帖子信息并统计每个帖子的回复数量
     *
     * @return 帖子列表
     */
    @Override
    public List<Post> findAllPost() {

        List<Post> postList = postMapper.findAllPost();

        // 遍历查询每个帖子的回复数量
        for (Post post : postList) {
            post.setReplyCount(replyMapper.getReplyCountByPid(post.getPid()));
        }

        return postList;
    }

    /**
     * 根据帖子ID查询帖子详情
     * 获取指定帖子的详细信息并统计回复数量
     *
     * @param pid 帖子ID
     * @return 帖子对象
     */
    @Override
    public Post findPostByPid(Long pid) {
        Post post = postMapper.findPostById(pid);
        // 查询该帖子的回复数量
        post.setReplyCount(replyMapper.getReplyCountByPid(pid));
        return post;
    }

    /**
     * 删除帖子
     * 删除指定帖子及其所有回复和收藏
     *
     * @param pid 帖子ID
     */
    @Override
    public void deletePost(Long pid) {
        // 先删除该帖子的所有回复
        replyMapper.deleteReply(pid);
        // 删除该帖子的所有收藏
        favoriteService.deleteByPostId(pid);
        // 再删除帖子本身
        postMapper.deletePost(pid);
    }

    /**
     * 分页查询帖子
     * 按分页参数查询帖子列表并统计回复数量
     *
     * @param map 分页参数，包含startPage等信息
     * @return 分页对象，包含帖子列表
     */
    @Override
    public Page<Post> findPostByPage(Map<String, Long> map) {
        Page<Post> page = new Page<>();
        // 设置每页显示数量
        map.put("showPage", page.getShowCount().longValue());
        // 设置当前页码
        page.setCurrentPage((int)(map.get("startPage") + 1));
        // 计算起始位置
        map.replace("startPage", map.get("startPage") * page.getShowCount());
        // 查询当前页的帖子列表
        page.setModelList(postMapper.findPostByPage(map));

        // 获取总帖子数
        Integer postCount = postMapper.getPostCount();

        // 计算总页数
        page.setPageTotal(postCount % page.getShowCount() == 0 ? postCount / page.getShowCount() : (postCount / page.getShowCount()) + 1);

        // 查询每个帖子的回复数量
        for (Post post : page.getModelList()) {
            post.setReplyCount(replyMapper.getReplyCountByPid(post.getPid()));
        }
        return page;
    }

    /**
     * 根据用户ID查询帖子
     * @param uid 用户ID
     * @return List<Post> 帖子列表
     */
    @Override
    public List<Post> findPostsByUserId(Long uid) {
        List<Post> postList = postMapper.findPostsByUserId(uid);
        // 遍历查询每个帖子的回复数量
        for (Post post : postList) {
            post.setReplyCount(replyMapper.getReplyCountByPid(post.getPid()));
        }
        return postList;
    }

    /**
     * 更新帖子内容
     * @param post 帖子对象
     * @return void
     */
    @Override
    public void updatePostContent(Post post) {
        postMapper.updatePostContent(post);
    }
}
