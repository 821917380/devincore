package com.devin.core.network.result;

import com.devin.core.network.result.HttpResult;

/**
 * Created by apple on 2016/10/23.
 */

public class ApiException extends RuntimeException {
    private HttpResult httpResult ;

    public HttpResult getHttpResult() {
        return httpResult;
    }

    public void setHttpResult(HttpResult httpResult) {
        this.httpResult = httpResult;
    }

    public ApiException() {

    }

    public ApiException(HttpResult httpResult) {
        this.httpResult = httpResult ;
    }
}
