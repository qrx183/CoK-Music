package com.example.music.mapper;

import com.example.music.model.Music;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author qiu
 * @version 1.8.0
 */
@Mapper
public interface MusicMapper {
    int insert(String title,String singer,String time,String url,int userid);

    Music selectBySAndN(String singer,String title);

    Music selectById(int id);

    int deleteMusic(int id);

    List<Music> selectMusic();

    List<Music> selectMusicByName(String title);
}
