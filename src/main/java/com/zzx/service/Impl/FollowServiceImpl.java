package com.zzx.service.Impl;

import com.zzx.mapper.FollowMapper;
import com.zzx.model.Follow;
import com.zzx.model.Page;
import com.zzx.model.User;
import com.zzx.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private FollowMapper followMapper;

    @Override
    public void save(Follow follow) {
        follow.setFollowtime(new Date());
        followMapper.save(follow);
    }

    @Override
    public void delete(Integer uid, Integer followUid) {
        followMapper.delete(uid, followUid);
    }

    @Override
    public Follow findByUserAndFollowUser(Integer uid, Integer followUid) {
        return followMapper.findByUserAndFollowUser(uid, followUid);
    }

    @Override
    public Long countByFollowUid(Integer followUid) {
        return followMapper.countByFollowUid(followUid);
    }

    @Override
    public List<Follow> findByUserId(Integer uid) {
        return followMapper.findByUserId(uid);
    }

    @Override
    public Page<User> findFollowUsersByPage(Map<String, Object> map) {
        Page<User> page = new Page<>();

        Object startPageObj = map.get("startPage");
        Long startPage = startPageObj != null ? ((Number) startPageObj).longValue() : 0L;
        page.setCurrentPage(startPage != null ? (int)(startPage + 1) : 1);

        map.put("startPage", startPage * page.getShowCount());
        map.put("pageSize", page.getShowCount());

        List<User> users = followMapper.findFollowUsersByPage(map);
        page.setModelList(users != null ? users : new ArrayList<>());

        Long total = followMapper.countFollowUsers(map);
        int totalPages = total > 0 ? (int) Math.ceil((double) total / page.getShowCount()) : 1;
        page.setPageTotal(totalPages);

        return page;
    }

    @Override
    public Page<User> findFansUsersByPage(Map<String, Object> map) {
        Page<User> page = new Page<>();

        Object startPageObj = map.get("startPage");
        Long startPage = startPageObj != null ? ((Number) startPageObj).longValue() : 0L;
        page.setCurrentPage(startPage != null ? (int)(startPage + 1) : 1);

        map.put("startPage", startPage * page.getShowCount());
        map.put("pageSize", page.getShowCount());

        List<User> users = followMapper.findFansUsersByPage(map);
        page.setModelList(users != null ? users : new ArrayList<>());

        Long total = followMapper.countFansUsers(map);
        int totalPages = total > 0 ? (int) Math.ceil((double) total / page.getShowCount()) : 1;
        page.setPageTotal(totalPages);

        return page;
    }
}
