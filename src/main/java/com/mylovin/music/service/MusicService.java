package com.mylovin.music.service;

import com.mylovin.music.model.UserInfo;
import com.mylovin.music.model.UserMusic;

import java.util.List;

public interface MusicService {
    UserMusic save(UserMusic music);

    List<UserMusic> findByUserInfo(UserInfo userInfo);
}
