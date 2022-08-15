package com.example.music.controller;

import com.example.music.mapper.UserMapper;
import com.example.music.model.User;
import com.example.music.tools.Constant;
import com.example.music.tools.ResponseBodyMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author qiu
 * @version 1.8.0
 */

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @RequestMapping("/login")
    public ResponseBodyMessage login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest httpServletRequest){
        //这个user是在注册的时候将加密后的密码存储到数据库中
        User user = userMapper.selectByName(username);
        if(user == null){
            //登录失败
            System.out.println("登录失败");
            return new ResponseBodyMessage<>(-1,"用户名或密码错误",false);
        }else{

            boolean flag = bCryptPasswordEncoder.matches(password,user.getPassword());

//            在注册功能实现之后在使用这个判断密码的正确性
//            if(!flag){
//                return new ResponseBodyMessage<>(-1,"用户名或密码错误",user);
//            }
            //登录成功创建Session.
            httpServletRequest.getSession().setAttribute(Constant.USERINFO_SESSION_KEY,user);
            System.out.println("登陆成功");

            return new ResponseBodyMessage<>(0,"登陆成功",user);
            //登录成功
        }
    }

    @RequestMapping("/reg")
    public ResponseBodyMessage<Boolean> reg(@RequestParam("username") String username,@RequestParam("password") String password){
        if(username == null || password == null){
            return new ResponseBodyMessage<>(-1,"用户名和密码不能为空",false);
        }
        User curUser = userMapper.selectByName(username);
        if(curUser != null){
            return new ResponseBodyMessage<>(-1,"用户名已经存在",false);
        }
        int res = userMapper.insertUser(username,password);
        if(res != 1){
            return new ResponseBodyMessage<>(-1,"注册失败",false);
        }
        return new ResponseBodyMessage<>(0,"注册成功",true);
    }

    @RequestMapping("/forget")
    public ResponseBodyMessage<Boolean> forget(@RequestParam("username") String username,@RequestParam("password")String password){
        User user = userMapper.selectByName(username);
        if(user == null){
            return new ResponseBodyMessage<>(-1,"不存在该用户",false);
        }
        int res = userMapper.updateUser(username,password);
        if(res != 1){
            return new ResponseBodyMessage<>(-1,"找回失败",false);
        }
        return new ResponseBodyMessage<>(0,"找回成功",true);

    }

    @RequestMapping("/exit")
    public ResponseBodyMessage<Boolean> exit(HttpServletRequest httpServletRequest,HttpServletResponse httpServletResponse){
        HttpSession httpSession = httpServletRequest.getSession(false);
        httpSession.removeAttribute(Constant.USERINFO_SESSION_KEY);
        return new ResponseBodyMessage<>(0,"退出成功",true);
    }
}
