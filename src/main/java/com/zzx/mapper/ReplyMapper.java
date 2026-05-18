package com.zzx.mapper;

import com.zzx.model.Post;
import com.zzx.model.Reply;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ReplyMapper {

    /**
     * 查询帖子回复数
     *
     * @param pid
     * @return
     */
    long getReplyCountByPid(Long pid);


    /**
     * 根据帖子id查询回复
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
     * 删除帖子下的所有回复
     *
     * @param pid
     */
    void deleteReply(Long pid);


    /**
     * 分页查询帖子回复
     *
     * @param map
     * @return
     */
    List<Reply> findReplyByPidAndPage(Map<String, Long> map);


    /**
     * 根据回复id删除回复
     *
     * @param rid
     */
    void deleteReplyByRid(Long rid);

    /**
     * 切换回复置顶状态
     *
     * @param rid 回复ID
     * @param isSticky 置顶状态：1置顶，0取消置顶
     */
    void toggleReplySticky(@Param("rid") Long rid, @Param("isSticky") Integer isSticky);

    /**
     * 清除帖子的所有置顶回复（当取消某个回复的置顶时，确保只有一个置顶）
     *
     * @param pid 帖子ID
     * @param excludeRid 排除的回复ID（当前要置顶的回复）
     */
    void clearOtherStickyReplies(@Param("pid") Long pid, @Param("excludeRid") Long excludeRid);

}
