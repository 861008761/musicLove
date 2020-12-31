package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.model.ShiroToken;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.service.UserInfoService;
import com.mylovin.music.util.Md5SaltUtil;
import com.mylovin.music.util.RestResult;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
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

/**
 * https://blog.csdn.net/weixin_42236404/article/details/89319359
 */
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

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {

            ShiroToken token = new ShiroToken(username, password);
            token.setRememberMe(false);
            //最终调用core.shiro.UserRealm doGetAuthenticationInfo方法 进行校验
            SecurityUtils.getSubject().login(token);

            UserInfo token2 = (UserInfo) SecurityUtils.getSubject().getPrincipal();

            LOGGER.info(token2.getUsername() + "," + token2.getPassword());
            map.put("status", 200);
            map.put("message", "登录成功");

            /**
             * 获取登录之前的地址
             */
            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            String url = null;
            if (null != savedRequest) {
                url = savedRequest.getRequestUrl();
            }
            // 跳转地址
            map.put("back_url", url);
        } catch (DisabledAccountException e) {
            map.put("status", 500);
            map.put("message", "帐号已经禁用。");
        } catch (Exception e) {
            map.put("status", 500);
            map.put("message", "帐号或密码错误");
        }
        result.setMsg(map);
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