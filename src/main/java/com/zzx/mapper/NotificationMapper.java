package com.zzx.mapper;

import com.zzx.model.Notification;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NotificationMapper {
    void insert(Notification notification);
    List<Notification> findByUid(@Param("uid") int uid, @Param("offset") int offset, @Param("limit") int limit);
    List<Notification> findByUidAndType(@Param("uid") int uid, @Param("type") String type, @Param("offset") int offset, @Param("limit") int limit);
    Long countByUid(@Param("uid") int uid);
    Long countByUidAndType(@Param("uid") int uid, @Param("type") String type);
    Long countUnreadByUid(@Param("uid") int uid);
    List<Map<String, Object>> countUnreadGroupByType(@Param("uid") int uid);
    void markAsRead(@Param("nid") int nid);
    void markAllAsRead(@Param("uid") int uid);
    void deleteById(@Param("nid") int nid);
}
