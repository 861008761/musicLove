package com.mylovin.music.service.impl;

import com.mylovin.music.dao.UserInfoDao;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoDao userInfoDao;

    @Override
    public UserInfo findByUsername(String username) {
        System.out.println("UserInfoServiceImpl.findByUsername()");
        return userInfoDao.findByUsername(username);
    }

    @Override
    public UserInfo save(UserInfo userInfo) {
        System.out.println("UserInfoServiceImpl.save(UserInfo userInfo)");
        return userInfoDao.save(userInfo);
    }

    @Override
    public boolean validateActivatedUser(String username) {
        System.out.println("UserInfoServiceImpl.validateActivatedUser(String username)");
        return false;
    }

    @Override
    public UserInfo checkCode(String code) {
        return userInfoDao.findByCode(code);
    }

    @Override
    public void updateUserStatus(UserInfo user) {
        userInfoDao.updateState(user.getState(), user.getUsername());
    }
}