package com.newcoder.community.controller;

import com.newcoder.community.annotation.LoginRequired;
import com.newcoder.community.entity.User;
import com.newcoder.community.service.FellowService;
import com.newcoder.community.service.LikeService;
import com.newcoder.community.service.UserService;
import com.newcoder.community.util.CommunityConstant;
import com.newcoder.community.util.CommunityUtil;
import com.newcoder.community.util.HostHolder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author xiuxiaoran
 * @date 2022/4/25 20:58
 * 使用自定义的注解给拦截器进行拦截
 */
@Controller
@RequestMapping("user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;   //需要知道当前用户，存储在这里

    @Autowired
    private LikeService likeService;  //查询点赞数量

    @Autowired
    private FellowService fellowService;

    //#######################上传头像相关的设置
    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketKeyName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketKeyUrl;
    //########################################

    //访问账号设置页面 ，设置自定义标记，只有登录你才可以访问
    //设置头像上传云服务器的相关的配置信息
    @LoginRequired
    @RequestMapping(path = "/setting" , method = RequestMethod.GET)
    public String getSettingPage(Model model){
        //生成上传的凭证去操作云服务器

        //1.生成文件名称
        String fileName = CommunityUtil.generatedUUId();

        //2.设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody",CommunityUtil.getJSONString(0));

        //生成上传凭证
        Auth auth = Auth.create(accessKey,secretKey);
        //生成上传凭证
        String uploadToken = auth.uploadToken(headerBucketKeyName,fileName,3600,policy);
        model.addAttribute("uploadToken",uploadToken);
        model.addAttribute("fileName",fileName);

        return "/site/setting";
    }

    //更新头像路径到七牛云的服务器
    //逻辑处理响应按钮在setting.js 中
    @RequestMapping(path = "/header/url",method = RequestMethod.POST)
    @ResponseBody
    public String updateUrl(String fileName){
        if(fileName==null){
            return CommunityUtil.getJSONString(1,"文件名不能为空");
        }
        //服务器url
        String url = headerBucketKeyUrl + "/" + fileName;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(),url);
        return CommunityUtil.getJSONString(0,"上传成功");  //成功
    }

    //**********************************************
    //这里重新设置修改密码的功能，独立实现，必须要登录才可以修改密码
    @LoginRequired
    @RequestMapping(path = "/updatePassword" , method = RequestMethod.POST)
    public String updatePassword(Model model,String originPassword,String newPassword,String confirmPassword){
        //处理相关的逻辑
        User user = hostHolder.getUser();
        //密码不能为空
        if(StringUtils.isBlank(originPassword)){
            model.addAttribute("originMsg","原始密码不能为空");
            return "/site/setting";  //返回当前页面继续输入
        }
        if(StringUtils.isBlank(newPassword)){
            model.addAttribute("newPasswordMsg","新密码不能为空");
            return "/site/setting";  //返回当前页面继续输入
        }
        if(StringUtils.isBlank(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","确认密码不能为空");
            return "/site/setting";  //返回当前页面继续输入
        }

        //如果原始密码输入不正确，返回错误信息
        if(!user.getPassword().equals(CommunityUtil.md5(originPassword)+user.getSalt())){
            model.addAttribute("originMsg","原始密码输入不正确");
            return "/site/setting";  //返回当前页面继续输入
        }
        //原始密码输入正确
        //两次输入的密码是否一致，一致，okok，可以修改
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("newPasswordMsg","两次密码输入不一致");
            model.addAttribute("confirmPassword","两次密码输入不一致");
            return "/site/setting";  //返回当前页面继续输入
        }
        //验证都通过了，更新密码
        userService.updatePassword(user.getId(),CommunityUtil.md5(newPassword)+user.getSalt());
        return "redirect:/index";   //重定向会首页表示成功，不然就结束，其实可以写个页面通知说面修改完毕的，这里就没写
    }
    //******************************************************

    /*
       废弃了，将图片上传到云服务器存储即可
     */
    //更换录头像 ，必须要登
    @LoginRequired
    @RequestMapping(path = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImg, Model model){
        if(headerImg==null){
            model.addAttribute("error","没有选择任何文件!");
            return "/site/setting";  //返回当前页面继续上传
        }

        //获取图片名称
        String originalFilename = headerImg.getOriginalFilename();
        //获取图片后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","选择后缀为jpg,jepg,png的图片文件!");
            return "/site/setting";  //返回当前页面继续上传
        }

        //生成随机字符串
        String fileName = CommunityUtil.generatedUUId()+suffix;
        //确定存放的路劲
        File dest = new File(uploadPath+ "/" + fileName);
        try {
            headerImg.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器内部错误",e);
        }
        //上传文件成功,更新用户的头像信息(web访问的路劲)，这个是需要web的路劲才可以进行访问的
        //；例如： http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);  //更新路劲
        return "redirect:/index";
    }

    //手动输出图片，就和那个访问验证码是一样的
    //借用response来返回图片信息
    //java IO流就是空白 类的上面已经有user路劲了，所以路劲这样写就可以了。
    //浏览器响应图片
    /*
       废弃，访问本地的图片，但是现在是存放在云服务器上面，所以也是废弃
     */
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放的图片路径
        fileName = uploadPath + "/" +fileName;  //带上服务器存储的filename
        //输出图片
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片文件
        response.setContentType("/image/"+suffix);

        // 小括号自动关闭打开的流
        try (
                OutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(fileName);)
        {
            byte[] buffer = new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取图像失败"+e.getMessage());
        }
    }


    //个人主页查询，显示点赞等信息
    @RequestMapping(value = "/profile/{userId}",method = RequestMethod.GET)
    public String getUserProfile(@PathVariable("userId") int userId,Model model){
        User user = userService.findUserById(userId);
        if(user==null){
            throw new RuntimeException("用户不存在");
        }
        //用户的基本信息显示给页面
        model.addAttribute("user",user);
        //点赞数量,查询用户的被点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",likeCount);

        //关注的数量, 这里简化了只是假设实体是人进行关注
        long followeeCount =  fellowService.findFolloweeCount(userId,ENTITY_TYPE_USER);
        model.addAttribute("followeeCount",followeeCount);
        //被关注，也就是粉丝的数量
        long followerCount = fellowService.findFollowerCount(ENTITY_TYPE_USER,userId);  //被关注的数量，实体只能是人啊，不能说帖子关注
        model.addAttribute("followerCount",followerCount);
        //是否已经被关注
        boolean isFollowed = false;
        if(hostHolder.getUser()!=null){
            //必须保证登录 , 因为是当前用户在操做，所以用户id是当前用户id，userId是现在你点进去判断有没有fellow的实体。
            isFollowed = fellowService.isFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
        }
        model.addAttribute("isFollowed",isFollowed);
        return "site/profile";
    }
}
