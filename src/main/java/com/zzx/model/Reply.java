package com.zzx.model;

import java.util.Date;

/**
 * 回复实体类
 * 表示对帖子的回复信息
 */
public class Reply {
    private User user; // 回复用户
    private Post post; // 所属帖子
    private String replymessage; // 回复内容
    private Date replytime; // 回复时间
    private Long rid; // 回复ID，主键
    private Integer isSticky; // 是否置顶，0否1是

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getReplymessage() {
        return replymessage;
    }

    public void setReplymessage(String replymessage) {
        this.replymessage = replymessage;
    }

    public Date getReplytime() {
        return replytime;
    }

    public void setReplytime(Date replytime) {
        this.replytime = replytime;
    }

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
    }
}
