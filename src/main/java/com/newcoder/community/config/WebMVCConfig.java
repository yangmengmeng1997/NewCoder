package com.newcoder.community.config;

import com.newcoder.community.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author xiuxiaoran
 * @date 2022/4/25 17:27
 * 拦截器的配置，主要需要的是配置拦截器
 * 每一个写好的拦截器类都i需要在这里进行注册配置
 * 在登录时我们使用spring security做权限检查，废弃掉之前的即可
 */
@Configuration
public class WebMVCConfig implements WebMvcConfigurer {
    //配置拦截器
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;  //废弃

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;   //配置统计数据的拦截器

    @Override  //注册接口的方法实现
    public void addInterceptors(InterceptorRegistry registry) {
        //访问静态资源都是 域名+项目名（不用加static）直接访问之后的静态资源了
        // /**表示项目下面的static的所有文件夹，下面的所有静态文件
        //配置排除了不需要拦截的资源，配置了需要拦截的哪些资源
        registry.addInterceptor(alphaInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
        .addPathPatterns("/register","/login");

        //注册拦截器,所有的都处理，不需要对特定的请求进行相应，所有请求都需要进行过滤拦截
        //之前指定了/register 和login的话/index 就没有进行过滤
        registry.addInterceptor(loginTicketInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        //排除拦截静态资源
        //registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        //排除拦截静态资源
        registry.addInterceptor(messageInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(dataInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }
}
