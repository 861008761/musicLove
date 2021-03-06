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
    private Object msg;

    public int getRetCode() {
        return retCode;
    }

    public Object getMsg() {
        return msg;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }
}
