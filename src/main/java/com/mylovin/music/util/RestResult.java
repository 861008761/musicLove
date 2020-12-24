package com.mylovin.music.util;

/**
 * 处理结果
 */
public class RestResult {
    /**
     * 返回码
     */
    private int retCode;
    /**
     * 返回消息
     */
    private String msg;

    public int getRetCode() {
        return retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
