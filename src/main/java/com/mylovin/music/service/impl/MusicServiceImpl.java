package com.mylovin.music.service.impl;

import com.mylovin.music.dao.MusicDao;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.model.UserMusic;
import com.mylovin.music.service.MusicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MusicServiceImpl implements MusicService {
    @Resource
    private MusicDao musicDao;

    @Override
    public UserMusic save(UserMusic music) {
        return musicDao.save(music);
    }

    @Override
    public List<UserMusic> findByUserInfo(UserInfo userInfo) {
        return musicDao.findByUserInfo(userInfo);
    }
}
