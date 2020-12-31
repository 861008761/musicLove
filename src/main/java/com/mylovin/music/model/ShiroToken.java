package com.mylovin.music.model;

import org.apache.shiro.authc.UsernamePasswordToken;

import java.io.Serializable;

public class ShiroToken extends UsernamePasswordToken implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2013574731436985473L;

    public ShiroToken(String username, String pswd) {
        super(username, pswd);
        this.pswd = pswd;
    }

    private String pswd;

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }
}