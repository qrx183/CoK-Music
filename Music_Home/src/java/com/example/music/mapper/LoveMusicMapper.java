package com.example.music.mapper;

import com.example.music.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qiu
 * @version 1.8.0
 */
@Mapper
public interface LoveMusicMapper {
    Music selectLoveMusic(int userid,int musicid);

    int deleteLoveMusicByMusicId(int musicid);

    int insertLoveMusic(int userid,int musicid);

    int deleteLoveMusic(int userid,int musicid);

    List<Music> selectLoveMusicByUserId(int userid);

    List<Music> selctLoveMusicListByNameAndUserId(String title,int userid);
}
