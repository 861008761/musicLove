package com.mylovin.music.service;

import com.mylovin.music.model.UserInfo;

public interface UserInfoService {
    /**
     * 通过username查找用户信息;
     */
    UserInfo findByUsername(String username);

    UserInfo save(UserInfo userInfo);

    boolean validateActivatedUser(String username);

    /**
     * 点击邮箱中的激活码进行激活，根据激活码查询用户，之后再进行修改用户状态为1进行激活
     * @param code
     * @return
     */
    UserInfo checkCode(String code);

    /**
     * 激活账户，修改用户状态为“1”进行激活
     * @param user
     */
    void updateUserStatus(UserInfo user);
}