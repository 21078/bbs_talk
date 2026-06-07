package com.zzx.service.Impl;

import com.zzx.mapper.NotificationMapper;
import com.zzx.model.Notification;
import com.zzx.model.Page;
import com.zzx.service.NotificationService;
import com.zzx.service.UserService;
import com.zzx.websocket.NotificationWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private UserService userService;

    @Override
    public void notify(Integer uid, String type, String content, Integer fromUid, String fromUname, Integer pid, String ptitle) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setType(type);
        notification.setContent(content);
        notification.setFromUid(fromUid);
        notification.setFromUname(fromUname);
        notification.setPid(pid);
        notification.setPtitle(ptitle);
        notificationMapper.insert(notification);

        String json = String.format(
            "{\"nid\":%d,\"type\":\"%s\",\"content\":\"%s\",\"fromUid\":%d,\"fromUname\":\"%s\",\"pid\":%d,\"ptitle\":\"%s\",\"isRead\":0}",
            notification.getNid(), type, content.replace("\"", "\\\""), fromUid, fromUname, pid != null ? pid : 0, ptitle != null ? ptitle.replace("\"", "\\\"") : ""
        );
        NotificationWebSocketHandler.sendToUser(uid, json);
    }

    @Override
    public void notifyAllUsers(String type, String content, Integer fromUid, String fromUname) {
        List<Integer> allUids = userService.findAllUids();
        for (Integer uid : allUids) {
            Notification notification = new Notification();
            notification.setUid(uid);
            notification.setType(type);
            notification.setContent(content);
            notification.setFromUid(fromUid);
            notification.setFromUname(fromUname);
            notification.setPid(null);
            notification.setPtitle(null);
            notificationMapper.insert(notification);

            String json = String.format(
                "{\"nid\":%d,\"type\":\"%s\",\"content\":\"%s\",\"fromUid\":%d,\"fromUname\":\"%s\",\"pid\":0,\"ptitle\":\"\",\"isRead\":0}",
                notification.getNid(), type, content.replace("\"", "\\\""), fromUid, fromUname
            );
            NotificationWebSocketHandler.sendToUser(uid, json);
        }
    }

    @Override
    public Page<Notification> getList(Integer uid, int page, String type) {
        Page<Notification> pageResult = new Page<>();
        pageResult.setCurrentPage(page);
        int offset = (page - 1) * pageResult.getShowCount();
        List<Notification> list;
        Long total;
        if (type != null && !type.isEmpty() && !"all".equals(type)) {
            list = notificationMapper.findByUidAndType(uid, type, offset, pageResult.getShowCount());
            total = notificationMapper.countByUidAndType(uid, type);
        } else {
            list = notificationMapper.findByUid(uid, offset, pageResult.getShowCount());
            total = notificationMapper.countByUid(uid);
        }
        pageResult.setModelList(list);
        int totalPages = (int) Math.ceil((double) total / pageResult.getShowCount());
        pageResult.setPageTotal(Math.max(totalPages, 1));
        return pageResult;
    }

    @Override
    public long getUnreadCount(Integer uid) {
        return notificationMapper.countUnreadByUid(uid);
    }

    @Override
    public Map<String, Long> getUnreadCountByTypeMap(Integer uid) {
        List<Map<String, Object>> list = notificationMapper.countUnreadGroupByType(uid);
        Map<String, Long> result = new HashMap<>();
        result.put("reply", 0L);
        result.put("favorite", 0L);
        result.put("follow", 0L);
        result.put("system", 0L);
        for (Map<String, Object> row : list) {
            String type = (String) row.get("type");
            Long count = (Long) row.get("count");
            result.put(type, count);
        }
        return result;
    }

    @Override
    public void markRead(int nid) {
        notificationMapper.markAsRead(nid);
    }

    @Override
    public void markAllRead(Integer uid) {
        notificationMapper.markAllAsRead(uid);
    }

    @Override
    public void delete(int nid) {
        notificationMapper.deleteById(nid);
    }
}
