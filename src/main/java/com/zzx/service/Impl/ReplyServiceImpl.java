package com.zzx.service.Impl;

import com.zzx.mapper.PostMapper;
import com.zzx.mapper.ReplyMapper;
import com.zzx.mapper.UserMapper;
import com.zzx.model.Page;
import com.zzx.model.Post;
import com.zzx.model.Reply;
import com.zzx.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 回复服务实现类
 * 实现回复相关的业务逻辑，包括发表回复、查询、删除等
 */
@Service
@Transactional
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据帖子ID查询回复列表
     * 获取指定帖子的所有回复信息
     *
     * @param pid 帖子ID
     * @return 回复列表
     */
    @Override
    public List<Reply> findReplyByPid(Long pid) {
        return replyMapper.findReplyByPid(pid);
    }

    /**
     * 保存回复
     * 保存新回复并更新帖子的最后回复时间
     *
     * @param reply 回复对象
     */
    @Override
    public void saveReply(Reply reply) {
        // 更新帖子的最后回复时间
        postMapper.updatePostLastReplyTime(reply.getPost());
        // 保存回复到数据库
        replyMapper.saveReply(reply);
    }

    /**
     * 根据帖子ID分页查询回复
     * 按分页参数查询指定帖子的回复列表
     *
     * @param map 分页参数，包含pid和startPage等信息
     * @return 分页对象，包含回复列表
     */
    @Override
    public Page<Reply> findReplyByPidAndPage(Map<String, Long> map) {
        Page<Reply> page = new Page<>();
        page.setShowCount(10); // 设置每页显示10条记录
        map.put("showPage", page.getShowCount().longValue());
        page.setCurrentPage((int)(map.get("startPage") + 1)); // 设置当前页码
        map.replace("startPage", map.get("startPage") * page.getShowCount()); // 计算起始位置
        page.setModelList(replyMapper.findReplyByPidAndPage(map)); // 查询当前页的回复列表

        // 获取该帖子的总回复数
        Integer postCount = (int)replyMapper.getReplyCountByPid(map.get("pid").longValue());
        // 计算总页数
        page.setPageTotal(postCount % page.getShowCount() == 0 ? postCount / page.getShowCount() : (postCount / page.getShowCount()) + 1);
        return page;
    }

    /**
     * 根据回复ID删除回复
     * 删除指定的回复记录
     *
     * @param rid 回复ID
     */
    @Override
    public void deleteReplyRid(Long rid) {
        replyMapper.deleteReplyByRid(rid);
    }

    /**
     * 切换回复置顶状态
     * 只有帖子创建者可以操作，每个帖子只能有一个置顶回复
     * 如果是问答板块的帖子，置顶回复时会给回复者奖励积分
     *
     * @param rid 回复ID
     * @param uid 用户ID（用于验证权限）
     * @param pid 帖子ID（用于验证权限）
     * @param action 操作：sticky置顶，unsticky取消置顶
     * @return 操作结果消息
     */
    @Override
    @Transactional
    public String toggleReplySticky(Long rid, Long uid, Long pid, String action) {
        // 验证用户是否为帖子创建者
        if (!postMapper.isPostCreator(pid, uid)) {
            return "只有帖子创建者可以操作回复置顶";
        }

        // 检查是否为问答板块的帖子
        Post post = postMapper.findPostById(pid);
        boolean isQuestionPost = post != null && "问题".equals(post.getCategory()) && post.getPrize() != null && post.getPrize() > 0;

        if ("sticky".equals(action)) {
            // 置顶回复：先清除该帖子的其他置顶回复，再置顶当前回复
            replyMapper.clearOtherStickyReplies(pid, rid);

            // 检查当前回复是否已经是置顶状态
            Reply currentReply = replyMapper.findReplyById(rid);
            if (currentReply != null && (currentReply.getIsSticky() == null || currentReply.getIsSticky() == 0)) {
                // 只有从非置顶变为置顶状态才奖励积分
                if (isQuestionPost) {
                    // 给回复者奖励积分
                    userMapper.addUserScore(currentReply.getUser().getUid(), post.getPrize());
                }
            }

            replyMapper.toggleReplySticky(rid, 1);
            return "置顶成功";
        } else if ("unsticky".equals(action)) {
            // 取消置顶前检查当前状态
            Reply currentReply = replyMapper.findReplyById(rid);
            if (currentReply != null && currentReply.getIsSticky() != null && currentReply.getIsSticky() == 1) {
                // 只有从置顶变为非置顶状态才扣除积分
                if (isQuestionPost) {
                    // 扣除回复者的积分，检查是否成功
                    int affectedRows = userMapper.deductUserScore(currentReply.getUser().getUid(), post.getPrize());
                    if (affectedRows == 0) {
                        // 积分扣除失败，可能是积分不足，但仍然允许取消置顶
                        // 这里可以选择记录日志或采取其他措施
                    }
                }
            }

            replyMapper.toggleReplySticky(rid, 0);
            return "取消置顶成功";
        } else {
            return "操作参数错误";
        }
    }

    /**
     * 根据用户ID删除该用户的所有回复
     * @param uid 用户ID
     */
    @Override
    public void deleteRepliesByUserId(Long uid) {
        replyMapper.deleteRepliesByUserId(uid);
    }

    /**
     * 检查用户是否为回复的创建者
     * @param rid 回复ID
     * @param uid 用户ID
     * @return true表示是该回复的创建者
     */
    @Override
    public boolean isReplyCreator(Long rid, Long uid) {
        return replyMapper.isReplyCreator(rid, uid) > 0;
    }

    /**
     * 检查用户是否为回复所属帖子的创建者
     * @param rid 回复ID
     * @param uid 用户ID
     * @return true表示是该回复所属帖子的创建者
     */
    @Override
    public boolean isPostCreatorByReplyId(Long rid, Long uid) {
        return replyMapper.isPostCreatorByReplyId(rid, uid) > 0;
    }
}
