package com.mylovin.music.dao;

import com.mylovin.music.model.UserInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserInfoDao extends CrudRepository<UserInfo, Long> {
    /**
     * 通过username查找用户信息;
     */
    UserInfo findByUsername(String username);

    @Override
    Optional<UserInfo> findById(Long id);

    @Override
    UserInfo save(UserInfo userInfo);
}