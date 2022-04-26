package com.newcoder.community;

import com.newcoder.community.dao.DiscussPostMapper;
import com.newcoder.community.dao.LoginTicketMapper;
import com.newcoder.community.dao.UserMapper;
import com.newcoder.community.entity.DiscussPost;
import com.newcoder.community.entity.LoginTicket;
import com.newcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author xiuxiaoran
 * @date 2022/4/20 12:27
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests{
    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private DiscussPostMapper discussPostMapper;

    @Autowired(required = false)
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void testSelectUser(){
       User user =  userMapper.selectById(155);
       System.out.println(user);
    }

    @Test
    public void testInsertUser(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("adc");
        user.setEmail("1111@qq.com");
        user.setHeaderUrl("http://www.newcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
    }

    @Test
    public void testUpdateUser(){
        int rows = userMapper.updateHeader(155,"http://www.newcoder.com/102.png");
        System.out.println(rows);

        int row = userMapper.updatePassword(155,"hello");
        System.out.println(row);
    }

    @Test
    public void testselectDiscussPosts(){
           List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 10);
           for(DiscussPost d : discussPosts){
              System.out.println(d);
       }
           int rows = discussPostMapper.selectDiscussPostRows(149);
           System.out.println(rows);
    }

    @Test
    public void testLoginTicketMapper(){
//        LoginTicket loginTicket =  new LoginTicket();
//        loginTicket.setUserId(101);
//        loginTicket.setTicket("asgafdhdsgf");
//        loginTicket.setStatus(1);
//        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
//        int row = loginTicketMapper.insertLogin(loginTicket);
//        System.out.println(row);
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("asgafdhdsgf");
        System.out.println(loginTicket);
        int rows = loginTicketMapper.updateStatus("asgafdhdsgf",0);
        System.out.println(rows);
    }

}
