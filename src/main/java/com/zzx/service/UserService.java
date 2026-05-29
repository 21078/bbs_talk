package com.zzx.service;

import com.zzx.exception.MessageException;
import com.zzx.model.User;

import java.util.List;

public interface UserService {

    /**
     * 注册
     *
     * @param user
     * @throws MessageException
     */
    void register(User user) throws MessageException;

    /**
     * 登录
     *
     * @param user
     * @return
     */
    User login(User user);


    /**
     * 查询所有用户
     */
    List<User> findAllUser();


    /**
     * 禁言用户
     *
     * @param user
     */
    void banUser(User user);

    /**
     * 禁言用户
     *
     * @param user
     */
    void unbanUser(User user);

    /**
     * uid查询用户
     */
    User findUserByUid(Integer uid);


    /**
     * 更改用户密码
     */
    void updatePassword(String uname, String oldPwd, String newPwd) throws MessageException;

    /**
     * 更新用户信息
     */
    void updateUser(User user);

    /**
     * 扣除用户积分
     *
     * @param uid 用户ID
     * @param score 要扣除的积分数
     * @return 扣除是否成功（false表示积分不足）
     */
    boolean deductUserScore(Integer uid, Integer score);

    /**
     * 为用户添加积分
     *
     * @param uid 用户ID
     * @param score 要添加的积分数
     */
    void addUserScore(Integer uid, Integer score);

    /**
     * 删除用户及其相关数据
     *
     * @param uid 用户ID
     */
    void deleteUser(Integer uid);
}
