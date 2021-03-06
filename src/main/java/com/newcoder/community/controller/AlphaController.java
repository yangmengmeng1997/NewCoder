package com.newcoder.community.controller;

import com.newcoder.community.service.AlphaService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiuxiaoran
 * @date 2022/4/18 22:10
 */
@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello(){
        return "Hello";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request , HttpServletResponse response) {
        //处理发送过来的请求，将response 响应数据返回过去
        //获取请求的相关数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        System.out.println(request.getParameter("code"));
        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        PrintWriter writer = null;  //获取响应数据里面的输出流，向浏览器输出
        try {
            writer = response.getWriter();
            writer.write("<h1>牛客网</h1>");  //可以看到这种向网页展示数据非常的繁琐，所以才会有之后的view出现
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            assert writer != null;
            writer.close();
        }
    }
    //在SpringMVC的框架下 怎么接收请求数据，怎么返回响应数据，可以看到对比上一种还是比较简单的 ？传参
    //GET 请求
    // /students?current=1?limit=10 例如查询学生，当前是第一页，每一页限制10个数据
    @RequestMapping(value = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@RequestParam(name="current", required = false , defaultValue = "1") int current,
                             @RequestParam(name="limit" , required = false , defaultValue = "10") int limit){
        System.out.println(current);
        System.out.println(limit);
        return "some Students";
    }

    //Restful 风格 路径传参
    //对比上一个请求响应变化，参数变为path传参 ， 在方法的形参获取上面变量也发生了变化
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";
    }

    //post请求提交数据
    @RequestMapping(path = "/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name,int age){  //post 请求中参数只要和表单中的一致就可以进行传参
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    //以上就是浏览器向服务器发送请求数据的两种方式了，我们以上的处理都是简单的将响应数据作为一个字符串返回，那么正规的返回数据时怎么样呢
    //比如返回一个动态的HTML呢。这种复杂数据结构？
    @RequestMapping(path = "/teacher" , method = RequestMethod.GET)
    public ModelAndView getTeacher(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",30);  // 添加model数据
        modelAndView.setViewName("/demo/view");  //templates 模板下面的demo/view.html ；模板文件下默认是.html文件
        return modelAndView;
    }

    @RequestMapping(path = "/school" , method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",20);
        return "/demo/view";
    }

    //响应JSON数据（异步请求）
    //java对象 -- JSON字符串 -- js对象
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp(){
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000);
        return map;   //返回浏览器的JSON字符串
    }

    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps(){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        map.put("salary",8000);
        list.add(map);

        map = new HashMap<>();
        map.put("name","李四");
        map.put("age",23);
        map.put("salary",8000);
        list.add(map);

        map = new HashMap<>();
        map.put("name","王五");
        map.put("age",23);
        map.put("salary",8000);
        list.add(map);

        return list;   //返回多个集合
    }



    //cookie实例，在客户机端存储，只能存字符串
    @RequestMapping(path="/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //创建cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generatedUUId());
        //设置cookie的生效范围,设置为在指定路劲以及其子路劲下才有效
        cookie.setPath("/community/alpha");
        //存储cookie，设置存储时间，不设置关掉浏览器就结束了
        cookie.setMaxAge(60*10);  //有效10分钟
        //发送cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String cookie){
        //将RequestBody中的键为“code”的赋值给形参cookie，即可获得cookie的值
        System.out.println(cookie);
        return "get cookie";
    }

    //测试Session , session可以存其他类型的数据都可以存储，不用创建对象，框架自动将session对象注入
    @RequestMapping(path="/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","Test");
        return "set session";
    }

    //测试Session
    @RequestMapping(path="/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session){
        //session获取数据，在服务器端存储
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    //测试ajax 提交数据所以用post请求
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
