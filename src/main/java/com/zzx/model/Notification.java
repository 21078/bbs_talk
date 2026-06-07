package com.zzx.model;

import java.util.Date;

public class Notification {
    private Integer nid;
    private Integer uid;
    private String type;
    private String content;
    private Integer fromUid;
    private String fromUname;
    private Integer pid;
    private String ptitle;
    private Integer isRead;
    private Date createTime;

    public Integer getNid() { return nid; }
    public void setNid(Integer nid) { this.nid = nid; }

    public Integer getUid() { return uid; }
    public void setUid(Integer uid) { this.uid = uid; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getFromUid() { return fromUid; }
    public void setFromUid(Integer fromUid) { this.fromUid = fromUid; }

    public String getFromUname() { return fromUname; }
    public void setFromUname(String fromUname) { this.fromUname = fromUname; }

    public Integer getPid() { return pid; }
    public void setPid(Integer pid) { this.pid = pid; }

    public String getPtitle() { return ptitle; }
    public void setPtitle(String ptitle) { this.ptitle = ptitle; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}
