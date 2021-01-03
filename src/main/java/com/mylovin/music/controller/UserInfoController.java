package com.mylovin.music.controller;

import com.alibaba.fastjson.JSON;
import com.mylovin.music.model.UserInfo;
import com.mylovin.music.util.ResultMessage;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/userInfo")
public class UserInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserInfoController.class);

    /**
     * 用户查询.
     *
     * @return
     */
    @RequestMapping("/userList")
    @RequiresPermissions("userInfo:view")//权限管理;
    public String userInfo() {
        return "userInfo";
    }

    /**
     * 用户添加;
     *
     * @return
     */
    @RequestMapping("/userAdd")
    @RequiresPermissions("userInfo:add")//权限管理;
    public String userInfoAdd() {
        return "userInfoAdd";
    }

    /**
     * 用户删除;
     *
     * @return
     */
    @RequestMapping("/userDel")
    @RequiresPermissions("userInfo:del")//权限管理;
    public String userDel() {
        return "userInfoDel";
    }

    @RequestMapping(value = "/userInfo/get", method = RequestMethod.GET)
    @ResponseBody
    public String userInfoGet(@RequestParam("sessionId") String sessionId) {
        ResultMessage message = new ResultMessage();
        Map<String, Object> msg = message.getMsg();
        UserInfo userInfo = (UserInfo) SecurityUtils.getSubject().getPrincipal();
        if (Objects.isNull(userInfo)) {
            message.setStatus(404);
            msg.put("message", "用户不存在");
        }

        message.setStatus(200);
        msg.put("message", "获取用户信息成功!");
        msg.put("userInfo", userInfo);
        LOGGER.info("UserInfoController.logout()");
        return JSON.toJSONString(message);
    }
}