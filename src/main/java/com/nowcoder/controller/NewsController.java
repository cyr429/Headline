package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@Controller
public class NewsController {
    private static  final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    NewsService newsService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String name,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR + name)), response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片失败"+e.getMessage());
        }
    }

    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                // 设置一个匿名用户
                news.setUserId(3);
            }
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }


    @RequestMapping(path={"/uploadImage/"},method={RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile files[]){
        try{
            String imageNameSet=new String();
            for(MultipartFile file : files){
                String fileUrl = newsService.saveImage(file);
                imageNameSet+=fileUrl;
                if (fileUrl==null){
                    return ToutiaoUtil.getJSONString(1,"上传失败");
                }
            }


            return ToutiaoUtil.getJSONString(0, imageNameSet);
        }catch(Exception e){
            logger.error("上传失败"+e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传失败");
        }

    }
}
