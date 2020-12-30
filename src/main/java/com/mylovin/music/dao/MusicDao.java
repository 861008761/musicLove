package com.mylovin.music.dao;

import com.mylovin.music.model.UserInfo;
import com.mylovin.music.model.UserMusic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MusicDao extends CrudRepository<UserMusic, Long> {
    UserMusic save(UserMusic music);
    
    List<UserMusic> findByUserInfo(UserInfo userInfo);
}