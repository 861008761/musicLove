package com.mylovin.music.service;

import com.mylovin.music.model.UserInfo;

public interface UserInfoService {
    /**
     * 通过username查找用户信息;
     */
    UserInfo findByUsername(String username);

    UserInfo save(UserInfo userInfo);
}