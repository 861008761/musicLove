package com.mylovin.music.util;

import java.util.HashMap;
import java.util.Map;

public class ResultMessage {
    private int status;
    private Map<String, Object> msg;

    public ResultMessage() {
        msg = new HashMap<>();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Object> getMsg() {
        return msg;
    }

    public void setMsg(Map<String, Object> msg) {
        this.msg = msg;
    }
}
