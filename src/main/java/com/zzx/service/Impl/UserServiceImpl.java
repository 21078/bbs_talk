package com.zzx.service.Impl;

import com.zzx.exception.MessageException;
import com.zzx.mapper.UserMapper;
import com.zzx.model.User;
import com.zzx.service.UserService;
import com.zzx.service.PostService;
import com.zzx.service.ReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
/**
 * 用户服务实现类
 * 实现用户相关的业务逻辑，包括注册、登录、权限管理等
 */
public class UserServiceImpl implements UserService {


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostService postService;

    @Autowired
    private ReplyService replyService;


    /**
     * 用户注册
     * 将新用户信息保存到数据库
     *
     * @param user 用户对象
     * @throws MessageException 当用户名已存在时抛出异常
     */
    @Override
    public void register(User user) throws MessageException {
        try {
            userMapper.save(user);
        } catch (RuntimeException e) {
            throw new MessageException("用户名已存在");
        }
    }

    /**
     * 用户登录
     * 根据用户名和密码验证用户身份
     *
     * @param user 用户对象，包含登录信息
     * @return 登录成功的用户对象，失败返回null
     */
    @Override
    public User login(User user) {
        return userMapper.findUserByUnameAndUpwd(user);
    }

    /**
     * 查询所有用户
     * 获取系统中所有用户的信息
     *
     * @return 用户列表
     */
    @Override
    public List<User> findAllUser() {
        return userMapper.findAllUser();
    }

    @Override
    public List<Integer> findAllUids() {
        return userMapper.findAllUids();
    }

    /**
     * 禁言用户
     * 将指定用户设置为禁言状态
     *
     * @param user 用户对象
     */
    @Override
    public void banUser(User user) {
        user.setUstate(0); // 0表示禁言状态
        userMapper.updateUser(user);
    }

    /**
     * 解禁用户
     * 将指定用户解除禁言状态
     *
     * @param user 用户对象
     */
    @Override
    public void unbanUser(User user) {
        user.setUstate(1); // 1表示正常状态
        userMapper.updateUser(user);
    }


    /**
     * 根据用户ID查询用户
     * 通过用户ID获取用户详细信息
     *
     * @param uid 用户ID
     * @return 用户对象
     */
    @Override
    public User findUserByUid(Integer uid) {
        return userMapper.findUserByUid(uid);
    }

    /**
     * 修改用户密码
     * 验证原密码正确后更新为新密码
     *
     * @param uname 用户名
     * @param oldPwd 原密码
     * @param newPwd 新密码
     * @throws MessageException 当原密码错误时抛出异常
     */
    @Override
    public void updatePassword(String uname, String oldPwd, String newPwd) throws MessageException {

        User user = new User();
        user.setUname(uname);
        user.setUpwd(oldPwd);
        User findUser = userMapper.findUserByUnameAndUpwd(user);

        if (null == findUser)
            throw new MessageException("原密码错误");

        user.setUpwd(newPwd);
        userMapper.updateUserPwd(user);
    }

    /**
     * 更新用户信息
     * 修改用户的个人信息
     *
     * @param user 用户对象，包含更新的信息
     */
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    /**
     * 扣除用户积分
     * 扣除用户指定数量的积分，如果积分不足则扣除失败
     *
     * @param uid 用户ID
     * @param score 要扣除的积分数
     * @return 扣除是否成功（false表示积分不足）
     */
    @Override
    public boolean deductUserScore(Integer uid, Integer score) {
        // 先检查用户积分是否足够
        User user = userMapper.findUserByUid(uid);
        if (user != null && user.getScore() != null && user.getScore() >= score) {
            // 执行扣除操作，并检查受影响的行数
            int affectedRows = userMapper.deductUserScore(uid, score);
            return affectedRows > 0;
        }
        return false;
    }

    /**
     * 为用户添加积分
     * 给指定用户添加积分
     *
     * @param uid 用户ID
     * @param score 要添加的积分数
     */
    @Override
    public void addUserScore(Integer uid, Integer score) {
        userMapper.addUserScore(uid, score);
    }

    /**
     * 删除用户及其所有相关数据
     * 包括用户的所有帖子、回复，最后删除用户账户
     *
     * @param uid 用户ID
     */
    @Override
    @Transactional
    public void deleteUser(Integer uid) {
        // 先删除用户的所有回复
        replyService.deleteRepliesByUserId(uid.longValue());

        // 再删除用户的所有帖子
        postService.deletePostsByUserId(uid.longValue());

        // 最后删除用户账户
        userMapper.deleteUser(uid);
    }

    /**
     * 切换用户认证状态
     * 如果当前已认证则设为未认证，反之亦然
     *
     * @param uid 用户ID
     */
    @Override
    public void toggleVerified(Integer uid) {
        User user = userMapper.findUserByUid(uid);
        if (user != null) {
            int newVerified = (user.getVerified() != null && user.getVerified() == 1) ? 0 : 1;
            userMapper.updateVerified(uid, newVerified);
        }
    }

    /**
     * 认证用户（设为已认证状态）
     *
     * @param uid 用户ID
     */
    @Override
    public void verifyUser(Integer uid) {
        User user = userMapper.findUserByUid(uid);
        if (user != null && (user.getVerified() == null || user.getVerified() == 0)) {
            userMapper.updateVerified(uid, 1);
        }
    }

    @Override
    public void banAllNonAdminUsers() {
        userMapper.banAllNonAdminUsers();
    }

    @Override
    public void unbanAllNonAdminUsers() {
        userMapper.unbanAllNonAdminUsers();
    }

}
