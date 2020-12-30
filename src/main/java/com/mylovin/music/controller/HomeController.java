package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.service.UserInfoService;
import com.mylovin.music.util.Md5SaltUtil;
import com.mylovin.music.util.RestResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/home")
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping({"/", "/index"})
    public String index() {
        return "/index";
    }

    /**
     * 用户登录
     *
     * @param request
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request, Map<String, Object> map) throws Exception {
        RestResult result = new RestResult();
        System.out.println("HomeController.login()");
        // 登录失败从request中获取shiro处理的异常信息。
        // shiroLoginFailure:就是shiro异常类的全类名.
        String exception = (String) request.getAttribute("shiroLoginFailure");
        System.out.println("exception=" + exception);
        String msg = "";
        if (exception != null) {
            if (UnknownAccountException.class.getName().equals(exception)) {
                System.out.println("UnknownAccountException -- > 账号不存在：");
                msg = "UnknownAccountException -- > 账号不存在：";
            } else if (IncorrectCredentialsException.class.getName().equals(exception)) {
                System.out.println("IncorrectCredentialsException -- > 密码不正确：");
                msg = "IncorrectCredentialsException -- > 密码不正确：";
            } else if ("kaptchaValidateFailed".equals(exception)) {
                System.out.println("kaptchaValidateFailed -- > 验证码错误");
                msg = "kaptchaValidateFailed -- > 验证码错误";
            } else {
                msg = "else >> " + exception;
                System.out.println("else -- >" + exception);
            }
            result.setRetCode(-1);
            result.setMsg(msg);
            return JSON.toJSONString(result);
        }
        Subject subject = SecurityUtils.getSubject();
        UserInfo user = (UserInfo) subject.getPrincipal();
        String sessionId = (String) subject.getSession().getId();
        Map<String, String> info = new HashMap<>();
        info.put("username", user.getUsername());
        info.put("sessionId", sessionId);
        result.setMsg(info);
        result.setRetCode(0);
        return JSON.toJSONString(result);
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    public String logout() {
        RestResult result = new RestResult();
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        result.setRetCode(0);
        result.setMsg("退出");
        return JSON.toJSONString(result);
    }

    /**
     * 用户注册
     * 赋予什么权限？
     * 上传 处理 下载 音频历史
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public String register(UserInfo userInfo) {
        RestResult result = new RestResult();
        if (null == userInfo) {
            LOGGER.error("non register info!");
            result.setMsg("non register info!");
            result.setRetCode(-1);
            return JSON.toJSONString(result);
        }
        UserInfo user = userInfoService.findByUsername(userInfo.getUsername());
        if (null != user) {
            LOGGER.error("user already exist!");
            result.setMsg("user exist!");
            result.setRetCode(-1);
            return JSON.toJSONString(result);
        }
        userInfo.setSalt(userInfo.getUsername() + Md5SaltUtil.generateSalt());
        userInfo.setPassword(Md5SaltUtil.encoderPassword(userInfo.getPassword(), userInfo.getSalt()));
        userInfoService.save(userInfo);
        LOGGER.info("register successfully!");
        result.setMsg("register successfully!");
        result.setRetCode(0);
        return JSON.toJSONString(result);
    }

    /**
     * 用户修改信息
     * 最好是前端明文传递
     *
     * @return
     */
    public String modifyProfile(@RequestParam("user") UserInfo userInfo) {
        RestResult result = new RestResult();

        return JSON.toJSONString(result);
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "403";
    }

}