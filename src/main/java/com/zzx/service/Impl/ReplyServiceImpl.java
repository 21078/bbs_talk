package com.zzx.service.Impl;

import com.zzx.mapper.PostMapper;
import com.zzx.mapper.ReplyMapper;
import com.zzx.model.Page;
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
}
