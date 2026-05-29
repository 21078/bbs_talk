package com.zzx.model;

import java.util.Date;

/**
 * 收藏实体类
 * 表示用户对帖子的收藏关系
 */
public class Favorite {

    private Long fid; // 收藏ID，主键
    private Integer uid; // 用户ID
    private Long pid; // 帖子ID
    private Date favtime; // 收藏时间

    // 关联对象
    private User user; // 收藏用户
    private Post post; // 被收藏的帖子

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Date getFavtime() {
        return favtime;
    }

    public void setFavtime(Date favtime) {
        this.favtime = favtime;
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
}