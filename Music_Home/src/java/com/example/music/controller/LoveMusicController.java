package com.example.music.controller;

import com.example.music.mapper.LoveMusicMapper;
import com.example.music.model.Music;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author qiu
 * @version 1.8.0
 */
@RestController
@RequestMapping("/lovemusic")
public class LoveMusicController {

    @Autowired
    LoveMusicMapper loveMusicMapper;

    /**
     * 收藏音乐
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/likeMusic")
    public ResponseBodyMessage<Boolean> likeMusic(@RequestParam("id") String id, HttpServletRequest httpServletRequest){
        int iid = Integer.parseInt(id);
        HttpSession httpSession = httpServletRequest.getSession(false);
        if(httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null){

            return new ResponseBodyMessage<>(-1,"当前处于未登录状态",false);
        }

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();

        Music music = loveMusicMapper.selectLoveMusic(userId,iid);
        if(music != null){
            //之前已经收藏过,这里可以实现取消收藏的功能
            //loveMusicMapper.deleteLoveMusic(userId,iid);
            return new ResponseBodyMessage<>(-1,"该音乐已被收藏",false);
        }

        int res = loveMusicMapper.insertLoveMusic(userId,iid);
        if(res == 1){
            return new ResponseBodyMessage<>(0,"收藏成功",true);
        }else{
            return new ResponseBodyMessage<>(-1,"收藏失败",false);
        }

    }

    /**
     * 查询收藏音乐
     */
    @RequestMapping("/selectLovemusic")
    public ResponseBodyMessage<List<Music>> selectLovemusic(@RequestParam(required = false,value = "title") String title,HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession(false);
        List<Music> list = null;
        if(httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null){

            return new ResponseBodyMessage<>(-1,"当前处于未登录状态",list);
        }

        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();

        if(title == null){
            list = loveMusicMapper.selectLoveMusicByUserId(userId);
        }else{
            list = loveMusicMapper.selctLoveMusicListByNameAndUserId(title,userId);
        }

        return new ResponseBodyMessage<>(0,"查询成功",list);
    }

    /**
     * 删除收藏音乐
     * @param id
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/deletelovemusic")
    public ResponseBodyMessage<Boolean> deleteLoveMusic(@RequestParam("id") String id,HttpServletRequest httpServletRequest){
        int musicId = Integer.parseInt(id);
        HttpSession httpSession = httpServletRequest.getSession(false);
        if(httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null){

            return new ResponseBodyMessage<>(-1,"当前处于未登录状态",false);
        }
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);

        int res = loveMusicMapper.deleteLoveMusic(user.getId(),musicId);
        if(res == 1){
            return new ResponseBodyMessage<>(0,"取消收藏成功",true);
        }else{
            return new ResponseBodyMessage<>(-1,"取消收藏失败",false);
        }
    }
}
