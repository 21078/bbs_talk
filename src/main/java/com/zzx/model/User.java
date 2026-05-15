package com.zzx.model;

import java.util.Date;
import java.util.List;

/**
 * 用户实体类
 * 表示系统中的用户信息
 */
public class User {
    private Integer uid; // 用户ID，主键
    private String uname; // 用户名
    private String upwd; // 密码
    private Integer ustate; // 用户状态：1正常，0禁用
    private Date ucreatetime; // 创建时间
    /**
     * 用户级别：0管理员，1普通用户
     */
    private Integer level;
    private String phone; // 联系电话
    private String career; // 职业
    private String address; // 工作地址
    private List<Post> postList; // 用户发布的帖子列表

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpwd() {
        return upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }

    public Integer getUstate() {
        return ustate;
    }

    public void setUstate(Integer ustate) {
        this.ustate = ustate;
    }

    public Date getUcreatetime() {
        return ucreatetime;
    }

    public void setUcreatetime(Date ucreatetime) {
        this.ucreatetime = ucreatetime;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", uname='" + uname + '\'' +
                ", upwd='" + upwd + '\'' +
                ", ustate=" + ustate +
                ", ucreatetime=" + ucreatetime +
                ", level=" + level +
                ", phone='" + phone + '\'' +
                ", career='" + career + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
