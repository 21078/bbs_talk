package com.zzx.service;

import com.zzx.model.Follow;
import com.zzx.model.Page;
import com.zzx.model.User;
import java.util.List;
import java.util.Map;

public interface FollowService {

    void save(Follow follow);

    void delete(Integer uid, Integer followUid);

    Follow findByUserAndFollowUser(Integer uid, Integer followUid);

    Long countByFollowUid(Integer followUid);

    List<Follow> findByUserId(Integer uid);

    Page<User> findFollowUsersByPage(Map<String, Object> map);

    Page<User> findFansUsersByPage(Map<String, Object> map);
}
