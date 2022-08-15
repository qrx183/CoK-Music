package com.example.music.controller;

import com.example.music.mapper.LoveMusicMapper;
import com.example.music.mapper.MusicMapper;
import com.example.music.mapper.UserMapper;
import com.example.music.model.Music;
import com.example.music.model.Music2;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author qiu
 * @version 1.8.0
 */
@RestController
@RequestMapping("/music")
public class MusicController {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MusicMapper musicMapper;

    @Resource
    private LoveMusicMapper loveMusicMapper;

    @Value("${music.local.path}")
    private String SAVE_PATH;

    /**
     * 上传音乐
     * @param singer
     * @param file
     * @param httpServletRequest
     * @return
     */
    @RequestMapping("/upload")
    public ResponseBodyMessage<Boolean> insertMusic(@RequestParam("singer") String singer,
                                                    @RequestParam("filename") MultipartFile file,
                                                    HttpServletRequest httpServletRequest,
                                                    HttpServletResponse httpServletResponse) {

        //验证登录
        HttpSession httpSession = httpServletRequest.getSession(false);

        if (httpSession == null || httpSession.getAttribute(Constant.USERINFO_SESSION_KEY) == null) {
            System.out.println("当前处于未登录状态,请先登录");
            return new ResponseBodyMessage<>(-1, "未登录", false);
        }
        String fileNameAndType = file.getOriginalFilename();
        //获取音乐信息
        int index = fileNameAndType.lastIndexOf('.');
        String title = fileNameAndType.substring(0, index);

        User user = (User) httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userid = user.getId();

        //http请求,用于后期播放音乐.
        String url = "/music/get?path=" + title;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = simpleDateFormat.format(new Date());

        //验证当音乐的singer和title相同时,认为是同一个歌曲,而同一个歌曲不能重复上传
        Music music = musicMapper.selectBySAndN(singer,title);
        if(music != null){
            return new ResponseBodyMessage<>(-1,"已存在相同歌曲",false);
        }


        //路径要是英文
        String path = SAVE_PATH + fileNameAndType;

        File dest = new File(path);
        if (!dest.exists()) {
            dest.mkdirs();
        }



        //通过MP3文件ID3V2中的最后128个字节中的前3个字节的是"TAG"来判断某个文件是不是MP3文件
        long length = file.getSize();
        //System.out.println(length);
        if (length < 128) {
            return new ResponseBodyMessage<>(-1, "非MP3文件", false);
        }
        try {
            InputStream fileInputStream = file.getInputStream();
            fileInputStream.skip(length - 128);

            byte[] buf = new byte[1024];
            int size = 0;
            String tag = "";
            while ((size = (fileInputStream.read(buf))) != -1) {
                tag += new String(buf, 0, size);
            }
            if (tag.length() < 128) {
                return new ResponseBodyMessage<>(-1, "非MP3文件", false);
            }
            tag = tag.substring(0, 3);
            if (!"TAG".equals(tag)) {
                return new ResponseBodyMessage<>(-1, "非MP3文件", false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(SAVE_PATH);

        try {
            //把dest文件上传到服务器上
            //这里有个问题:上传的file如果歌手不同的话应该是可以重复上传的.
            file.transferTo(dest);
            //   return new ResponseBodyMessage<>(0,"上传成功",true);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseBodyMessage<>(-1, "服务器上传失败", false);

        }

        //数据库上传



        int res = musicMapper.insert(title, singer, time, url, userid);
        if (res == 1) {
            //System.out.println("数据库上传成功");
            try {
                httpServletResponse.sendRedirect("/index1.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseBodyMessage<>(0, "上传成功", true);
        } else {
            return new ResponseBodyMessage<>(-1, "数据库上传失败", false);
        }

    }

    /**
     * 播放音乐
     * @param path:xxx.mp3
     * @return
     */
    @RequestMapping("/get")
    public ResponseEntity<byte[]> playMusic(String path){
        File file = new File(SAVE_PATH+ path);
        try {
            byte[] a = Files.readAllBytes(file.toPath());
            if(a == null){
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * 删除单个音乐:
     */
    @RequestMapping("/delete")
    public ResponseBodyMessage<Boolean> deleteMusic(@RequestParam("id") String id,HttpServletRequest httpServletRequest){
        int idd = Integer.parseInt(id);
        Music music = musicMapper.selectById(idd);
        HttpSession httpSession = httpServletRequest.getSession(false);
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        if(music.getUserid() != user.getId()){
            return new ResponseBodyMessage<>(-1,"您没有删除该音乐的权限",false);
        }
        if(music == null){
            return new ResponseBodyMessage<>(-1,"不存在这首音乐",false);
        }


        //删除数据库中的音乐.
        int res = musicMapper.deleteMusic(idd);
        if(res == 1){
            //删除服务器的音乐;
            String fileName = music.getTitle();
            File file = new File(SAVE_PATH + fileName + ".mp3");
            boolean flag = file.delete();
            if(flag){
                loveMusicMapper.deleteLoveMusicByMusicId(idd);
                return new ResponseBodyMessage<>(0,"服务器音乐删除成功",true);
            }else{
                return new ResponseBodyMessage<>(-1,"服务器音乐删除失败",false);
            }

        }else{
            return new ResponseBodyMessage<>(-1,"数据库音乐删除失败",false);
        }
    }

    /**
     * 批量删除音乐
     * 批量删除音乐这个功能这有个待优化的点:如果批量删除时有1个音乐删除失败,那么更加
     * 人性化的操作是批量删除的这所有音乐都不被删除
     * 也就是说最好再加个事务回滚的操作.
     */
    @RequestMapping("/deleteSel")
    public ResponseBodyMessage<Boolean> deleteMusicSel(@RequestParam("id[]") List<Integer> id,HttpServletRequest httpServletRequest){
        HttpSession httpSession = httpServletRequest.getSession(false);
        User user = (User)httpSession.getAttribute(Constant.USERINFO_SESSION_KEY);
        int userId = user.getId();
        for(int idd: id){
            Music music = musicMapper.selectById(idd);
            if(music == null){
                return new ResponseBodyMessage<>(-1,"没有你要删除的音乐",false);
            }
            if(music.getUserid() != userId){
                return new ResponseBodyMessage<>(-1,"您没有删除"+ music.getTitle() + "音乐的权限",false);
            }
            int res = musicMapper.deleteMusic(idd);
            if(res == 1){
                //删除服务器的音乐;
                String fileName = music.getTitle();
                File file = new File(SAVE_PATH + fileName + ".mp3");
                boolean flag = file.delete();
                if(!flag){
                    return new ResponseBodyMessage<>(-1,"服务器音乐删除失败",false);
                }
                loveMusicMapper.deleteLoveMusicByMusicId(idd);
            }else{
                return new ResponseBodyMessage<>(-1,"数据库音乐删除失败",false);
            }
        }
        return new ResponseBodyMessage<>(0,"批量删除成功",true);
    }

    /**
     * 查询音乐
     * 支持模糊查询和默认查询所有音乐.
     */

    @RequestMapping("/selectMusic")
    public ResponseBodyMessage<List<Music2>> selectMusic(@RequestParam(required = false,value = "title") String musicName){
        List<Music> list = null;

        if(musicName == null){
            list = musicMapper.selectMusic();
        }else{
            list = musicMapper.selectMusicByName(musicName);
        }
        List<Music2> ans = new ArrayList<>();
        for(Music m:list){
            int userId = m.getUserid();
            User user = userMapper.selectById(userId);
            Music2 music2 = new Music2();
            music2.setId(m.getId());
            music2.setSinger(m.getSinger());
            music2.setTime(m.getTime());
            music2.setTitle(m.getTitle());
            music2.setUserid(m.getUserid());
            music2.setUrl(m.getUrl());
            music2.setUsername(user.getUsername());
            ans.add(music2);
        }
        return new ResponseBodyMessage<>(0,"查询成功",ans);
    }



}
