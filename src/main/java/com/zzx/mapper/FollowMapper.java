package com.zzx.mapper;

import com.zzx.model.Follow;
import com.zzx.model.User;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper {

    @Insert("INSERT INTO follow(uid, follow_uid, followtime) VALUES(#{uid}, #{followUid}, #{followtime})")
    void save(Follow follow);

    @Delete("DELETE FROM follow WHERE uid = #{uid} AND follow_uid = #{followUid}")
    void delete(@Param("uid") Integer uid, @Param("followUid") Integer followUid);

    @Select("SELECT * FROM follow WHERE uid = #{uid} AND follow_uid = #{followUid}")
    Follow findByUserAndFollowUser(@Param("uid") Integer uid, @Param("followUid") Integer followUid);

    @Select("SELECT COUNT(*) FROM follow WHERE follow_uid = #{followUid}")
    Long countByFollowUid(Integer followUid);

    @Select("SELECT * FROM follow WHERE uid = #{uid} ORDER BY followtime DESC")
    List<Follow> findByUserId(Integer uid);

    List<User> findFollowUsersByPage(Map<String, Object> map);

    Long countFollowUsers(Map<String, Object> map);

    List<User> findFansUsersByPage(Map<String, Object> map);

    Long countFansUsers(Map<String, Object> map);
}
