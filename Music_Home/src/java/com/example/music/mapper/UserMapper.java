package com.example.music.mapper;

import com.example.music.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author qiu
 * @version 1.8.0
 */
@Mapper
public interface UserMapper {
    User login(User user);
    User selectByName(String username);
    User selectById(int id);
    int insertUser(String username,String password);
    int updateUser(String username,String password);
}
