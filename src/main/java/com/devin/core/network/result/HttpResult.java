package com.devin.core.network.result;

/**
 * Created by apple on 2016/10/23.
 */

public class HttpResult<T> {
    private String code;
    private String devMsg;
    private String userMsg;
    private String errorUuid;
    private T data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDevMsg() {
        return devMsg;
    }

    public void setDevMsg(String devMsg) {
        this.devMsg = devMsg;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public void setUserMsg(String userMsg) {
        this.userMsg = userMsg;
    }

    public String getErrorUuid() {
        return errorUuid;
    }

    public void setErrorUuid(String errorUuid) {
        this.errorUuid = errorUuid;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
