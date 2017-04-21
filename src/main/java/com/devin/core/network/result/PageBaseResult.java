package com.devin.core.network.result;

import java.util.ArrayList;

/**
 * Created by apple on 2016/11/1.
 */

public class PageBaseResult<T>  {
    private String code;
    private String devMsg;
    private String userMsg;
    private String errorUuid;
    private PageBean<T> data ;

    public ArrayList<T> getPageData() {
        if (data != null) {
            return data.data ;
        }
        return null ;
    }

    public int getPageSize(){
        if(data != null) {
            return data.pageSize;
        }
        return 0 ;
    }

    public int getPageNum() {
        if(data != null) {
            return data.pageNum;
        }
        return 0 ;
    }

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




    class PageBean<H> {
        private int pageSize ;
        private int pageNum ;
        private ArrayList<H> data ;
    }
}
