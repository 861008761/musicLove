package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.model.ShiroToken;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.service.MailService;
import com.mylovin.music.service.UserInfoService;
import com.mylovin.music.util.Md5SaltUtil;
import com.mylovin.music.util.RestResult;
import com.mylovin.music.util.ResultMessage;
import com.mylovin.music.util.UUIDUtils;
import com.mylovin.music.util.exception.NonActivateException;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * https://blog.csdn.net/weixin_42236404/article/details/89319359
 */
@Controller
@RequestMapping("/home")
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private MailService mailService;

    @Value("${server.ip}")
    private String ip;

    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${session.timeout}")
    private Long sessionTimeOut;

    @RequestMapping({"/", "/index"})
    public String index() {
        return "/index";
    }

    /**
     * 用户登录
     *
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public String login(HttpServletRequest request) throws Exception {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();

        System.out.println("HomeController.login()");

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        try {
            boolean activated = userInfoService.validateActivatedUser(username);
            if (!activated) {
                throw new NonActivateException("账号未激活");
            }

            ShiroToken token = new ShiroToken(username, password);
            token.setRememberMe(false);
            //最终调用core.shiro.UserRealm doGetAuthenticationInfo方法 进行校验
            SecurityUtils.getSubject().login(token);

            UserInfo token2 = (UserInfo) SecurityUtils.getSubject().getPrincipal();

            LOGGER.info(token2.getUsername() + "," + token2.getPassword());
            message.setStatus(200);
            msg.put("message", "登录成功");

            /**
             * 获取登录之前的地址
             */
            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            String url = null;
            if (null != savedRequest) {
                url = savedRequest.getRequestUrl();
            }
            // 跳转地址
            msg.put("back_url", url);

            //查询sessionId
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(true);

            LOGGER.info(String.valueOf(subject));
            LOGGER.info(String.valueOf(subject.getSession()));

            session.setTimeout(sessionTimeOut);//60秒
            String sessionId = (String) session.getId();
            msg.put("sessionId", sessionId);
        } catch (DisabledAccountException e) {
            message.setStatus(500);
            msg.put("message", "帐号已经禁用");
        } catch (NonActivateException e) {
            message.setStatus(500);
            msg.put("message", "账号未激活");
        } catch (Exception e) {
            message.setStatus(500);
            msg.put("message", "帐号或密码错误");
        }
        return JSON.toJSONString(message);
    }

    /**
     * 用户退出登录
     *
     * @return
     */
    @RequestMapping("/logout")
    @ResponseBody
    public String logout() {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();
        LOGGER.info("UserInfoController.logout()");
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
            message.setStatus(200);
            msg.put("message", "正常退出");
        } catch (Exception e) {
            message.setStatus(500);
            msg.put("message", "账号退出失败");
        }
        return JSON.toJSONString(message);
    }

    /**
     * 用户注册
     * 赋予什么权限？
     * 上传 处理 下载 音频历史
     * <p>
     * 注册的时候默认状态为0：未激活，并且调用邮件服务发送激活码到邮箱
     * 注册完之后，发送邮件给用户邮箱，用户点击立刻调用checkCode方法
     *
     * @param userInfo
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public String register(UserInfo userInfo) {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();

        if (null == userInfo
                || StringUtils.isEmpty(userInfo.getUsername())
                || StringUtils.isEmpty(userInfo.getPassword())
                || StringUtils.isEmpty(userInfo.getUseremail())) {
            LOGGER.error("register info not complete!");
            msg.put("message", "register info not complete!");
            message.setStatus(500);
            return JSON.toJSONString(message);
        }
        if (!validEmail(userInfo.getUseremail())) {
            LOGGER.error("user email is not legal!");
            msg.put("message", "user email is not legal!");
            message.setStatus(500);
            return JSON.toJSONString(message);
        }
        UserInfo user = userInfoService.findByUsername(userInfo.getUsername());
        if (null != user) {
            LOGGER.error("user already exist!");
            msg.put("message", "user exist!");
            message.setStatus(500);
            return JSON.toJSONString(message);
        }
        //1、生成用户基本信息
        userInfo.setSalt(userInfo.getUsername() + Md5SaltUtil.generateSalt());
        userInfo.setPassword(Md5SaltUtil.encoderPassword(userInfo.getPassword(), userInfo.getSalt()));
        userInfo.setState((byte) 0);

        //2、获取激活码
        String code = UUIDUtils.getUUID() + UUIDUtils.getUUID();
        userInfo.setCode(code);

        //3、注册
        userInfoService.save(userInfo);

        //4、发送邮件
        //邮件主题
        String subject = "来自您的制作音乐伴奏网站的激活邮件";
        //user/checkCode?code=code(激活码)是我们点击邮件链接之后根据激活码查询用户，如果存在说明一致，将用户状态修改为“1”激活
        //上面的激活码发送到用户注册邮箱
        String context = "<a href=\"http://" + ip + ":" + port + (StringUtils.isEmpty(contextPath) ? "" : "/" + contextPath) + "/home/checkCode?code=" + code + "\">激活请点击:" + code + "</a>";
        //发送激活邮件
        mailService.sendHtmlMail(userInfo.getUseremail(), subject, context);

        LOGGER.info("register successfully!");
        msg.put("message", "register successfully!");
        message.setStatus(200);
        return JSON.toJSONString(message);
    }

    /**
     * 校验邮箱中的code激活账户
     * 首先根据激活码code查询用户，之后再把状态修改为"1"
     */
    @RequestMapping(value = "/checkCode")
    @ResponseBody
    public String checkCode(String code) {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();

        UserInfo user = userInfoService.checkCode(code);
        //如果用户不等于null，把用户状态修改status=1
        if (user != null) {
            user.setState((byte) 1);
            //把code验证码清空，已经不需要了
            user.setCode("");
            System.out.println(user);

            try {
                userInfoService.updateUserStatus(user);
            } catch (Exception e) {
                e.printStackTrace();
            }

            message.setStatus(200);
            msg.put("message", "激活用户成功");
            return JSON.toJSONString(message);
        } else {
            message.setStatus(200);
            msg.put("message", "激活用户失败");
            return JSON.toJSONString(message);
        }
    }

    /**
     * 用户修改信息
     * 最好是前端明文传递
     *
     * @return
     */
    public String modifyProfile(@RequestParam("user") UserInfo userInfo) {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();

        return JSON.toJSONString(message);
    }

    @RequestMapping("/403")
    public String unauthorizedRole() {
        System.out.println("------没有权限-------");
        return "403";
    }

    /**
     * 合法E-mail地址：
     * 1. 必须包含一个并且只有一个符号“@”
     * 2. 第一个字符不得是“@”或者“.”
     * 3. 不允许出现“@.”或者.@
     * 4. 结尾不得是字符“@”或者“.”
     * 5. 允许“@”前的字符中出现“＋”
     * 6. 不允许“＋”在最前面，或者“＋@”
     */
    private boolean validEmail(String email) {
        String regex = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        return Pattern.compile(regex).matcher(email).find();
    }
}