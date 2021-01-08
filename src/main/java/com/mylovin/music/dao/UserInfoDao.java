package com.mylovin.music.dao;

import com.mylovin.music.model.UserInfo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserInfoDao extends CrudRepository<UserInfo, Long> {
    /**
     * 通过username查找用户信息;
     */
    UserInfo findByUsername(String username);

    UserInfo findByUseremail(String useremail);

    @Override
    Optional<UserInfo> findById(Long id);

    @Override
    UserInfo save(UserInfo userInfo);

    UserInfo findByCode(String code);

    @Transactional
    @Modifying
    @Query("update UserInfo u set u.state = ?1 where u.username = ?2")
    void updateState(byte state, String username);

    @Transactional
    @Modifying
    @Query("update UserInfo u set u.password = ?1 where u.username = ?2")
    void updatePassword(String password, String username);

    @Query("select u from UserInfo u where u.username = ?1 and u.state = 1")
    UserInfo validateActivatedUser(String username);
}