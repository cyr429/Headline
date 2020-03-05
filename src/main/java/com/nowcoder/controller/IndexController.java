package com.nowcoder.controller;

import com.nowcoder.aspect.LogAspect;
import com.nowcoder.model.User;
import com.nowcoder.service.ToutiaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.*;


@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);


    @Autowired
    private ToutiaoService ttsvc;

    @RequestMapping(path = {"/","/index"})
    @ResponseBody
    public String index(HttpSession session){
        logger.info("visit index");
        return "this is homepage. "+session.getAttribute("msg")+"<br>"+ttsvc.say();

    }

    @RequestMapping("/prop/{GId}/{UId}")
    @ResponseBody
    public String prop(@PathVariable("GId") String GId,
                       @PathVariable("UId") int UId,
                       @RequestParam(value = "type", defaultValue = "666") int type,
                       @RequestParam(value = "key",defaultValue = "nowcoder") String key){

        return String.format("{%s},{%d},{%d},{%s}",GId,UId,type,key);
    }

    @RequestMapping(value={"/vm"})
    public String news(Model model){
        model.addAttribute("value1","hey, its value1");
        List<String> colors= Arrays.asList(new String[] {"RED","GREEN","BLUE"});
        Map<String,String> map=new HashMap<String,String>();
        for(int i=0;i<4;++i){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("colors",colors);
        model.addAttribute("map",map);
        model.addAttribute("user",new User("Jim"));
        return "news";
    }

    @RequestMapping(value = {"/request"})
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name+":"+request.getHeader(name)+"<br>");
        }
        for(Cookie cookie : request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append("----");
            sb.append(cookie.getValue()+"<br>");
        }

        sb.append("Method:"+request.getMethod()+"<br>");
        sb.append("URI:"+request.getRequestURI()+"<br>");
        sb.append("QueryString:"+request.getQueryString()+"<br>");
        return sb.toString();
    }
    @RequestMapping(value = {"/response"})
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderid",defaultValue = "a") String nowcoderid,
                           @RequestParam(value="key", defaultValue = "key") String key,
                           @RequestParam(value="value",defaultValue = "value") String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "Nowcoderid from cookie:"+nowcoderid;
    }
    @RequestMapping("/redirect/{code}")
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession session){
        RedirectView red = new RedirectView("/",true);
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        session.setAttribute("msg","jump from redirect. ");
        return red;
    }

    @ResponseBody
    @RequestMapping("/admin")
    public String admin(@RequestParam(value = "key", required = false) String key) throws IllegalAccessException {
        if(key.equals("admin")){
            return "hello admin";
        }
        throw new IllegalAccessException("key error");
    }

    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error: "+e.getMessage();
    }

}
