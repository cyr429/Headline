package com.nowcoder.service;


import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by nowcoder on 2016/7/2.
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;


    public Map<String, Object> register(String username, String password){
        Map<String, Object> map= new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(username)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user= userDAO.selectByName(username);
        if(user!=null){
            map.put("msgname","用户名被注册");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));
        userDAO.addUser(user);

        //ticket
        String ticket= addLoginTicket(user.getId());
        map.put("ticket",ticket);

        //password strength
        return map;
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }



    public Map<String, Object> login(String username, String password){
        Map<String, Object> map= new HashMap<String, Object>();
        if(StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(username)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user= userDAO.selectByName(username);
        if(user==null){
            map.put("msgname","用户不存在");
            return map;
        }

        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd",user.getPassword()+"-----"+ToutiaoUtil.MD5(password+user.getSalt()));
            return map;
        }
        //ticket
        String ticket= addLoginTicket(user.getId());
        map.put("ticket",ticket);

        //login

        //password strength
        return map;
    }

    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();

    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
