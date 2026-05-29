package com.zzx.service;

import com.zzx.model.Page;
import com.zzx.model.Reply;

import java.util.List;
import java.util.Map;

public interface ReplyService {


    /**
     * 根据帖子id查询帖子回复
     *
     * @param pid
     * @return
     */
    List<Reply> findReplyByPid(Long pid);


    /**
     * 保存回复
     *
     * @param reply
     */
    void saveReply(Reply reply);

    /**
     * 根据帖子id分页查询回复
     *
     * @return
     */
    Page<Reply> findReplyByPidAndPage(Map<String, Long> map);


    /**
     * 根据回复id删除回复
     *
     * @param rid
     */
    void deleteReplyRid(Long rid);

    /**
     * 切换回复置顶状态
     * 只有帖子创建者可以操作
     *
     * @param rid 回复ID
     * @param uid 用户ID（用于验证权限）
     * @param pid 帖子ID（用于验证权限）
     * @param action 操作：sticky置顶，unsticky取消置顶
     * @return 操作结果消息
     */
    String toggleReplySticky(Long rid, Long uid, Long pid, String action);

    /**
     * 根据用户ID删除该用户的所有回复
     *
     * @param uid 用户ID
     */
    void deleteRepliesByUserId(Long uid);

    /**
     * 检查用户是否为回复的创建者
     *
     * @param rid 回复ID
     * @param uid 用户ID
     * @return true表示是该回复的创建者
     */
    boolean isReplyCreator(Long rid, Long uid);

    /**
     * 检查用户是否为回复所属帖子的创建者
     *
     * @param rid 回复ID
     * @param uid 用户ID
     * @return true表示是该回复所属帖子的创建者
     */
    boolean isPostCreatorByReplyId(Long rid, Long uid);
}
