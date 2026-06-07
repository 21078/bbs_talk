package com.zzx.service;

import com.zzx.model.Notification;
import com.zzx.model.Page;
import java.util.Map;

public interface NotificationService {
    void notify(Integer uid, String type, String content, Integer fromUid, String fromUname, Integer pid, String ptitle);
    void notifyAllUsers(String type, String content, Integer fromUid, String fromUname);
    Page<Notification> getList(Integer uid, int page, String type);
    long getUnreadCount(Integer uid);
    Map<String, Long> getUnreadCountByTypeMap(Integer uid);
    void markRead(int nid);
    void markAllRead(Integer uid);
    void delete(int nid);
}
