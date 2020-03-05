package com.nowcoder;
import com.nowcoder.dao.NewsDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.News;
import com.nowcoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.nowcoder.dao.UserDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class ToutiaoDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	NewsDAO newsDAO;

	@Test
	public void contextLoads() {
		for(int i = 0; i < 11; i++){
			User user=new User();
			user.setHead_url("http://images.nowcoder.com/head/null.png");
			user.setName(String.format("User%d",i+1));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);
			user.setPassword("newpassword");
			userDAO.updateUserPassword(user);
			News news=new News();
			news.setCommentCount(i*i);
			Date date=new Date();
			date.setTime(date.getTime()+1000*3600*5*1);
			news.setCreateDate(date);
			news.setImage("dm.png");
			news.setLikeCount(i+1);
			news.setUserId(i+1);
			news.setTitle(String.format("TITLE{%d}",i));
			news.setLink("http://www.baidu.com");
			newsDAO.addNews(news);
		}
		userDAO.deleteById(6);
	}

}
