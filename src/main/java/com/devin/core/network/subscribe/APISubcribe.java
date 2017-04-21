package com.devin.core.network.subscribe;

import com.devin.core.network.result.ApiException;
import com.devin.core.network.result.RetrofitException;
import com.google.gson.JsonParseException;
import com.google.gson.stream.MalformedJsonException;

import org.json.JSONException;

import java.text.ParseException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import timber.log.Timber;

/**
 * Created by apple on 2016/10/23.
 */
public abstract class APISubcribe<T> extends Subscriber<T> {
    //对应HTTP的状态码
    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final int NOT_FOUND = 404;
    private static final int REQUEST_TIMEOUT = 408;
    private static final int INTERNAL_SERVER_ERROR = 500;
    private static final int BAD_GATEWAY = 502;
    private static final int SERVICE_UNAVAILABLE = 503;
    private static final int GATEWAY_TIMEOUT = 504;

    private static final int RESULT_ERROR = -1000 ;

    private final String networkMsg = "网络异常";
    private final String parseMsg = "返回结果错误";
    private final String unknownMsg = "网络连接超时";

    public APISubcribe() {
    }

    @Override
    public void onError(Throwable e) {
        Throwable throwable = e;
        //获取最根源的异常
        while(throwable.getCause() != null){
            e = throwable;
            throwable = throwable.getCause();
        }
        Timber.e(e);

        RetrofitException ex;
        if (e instanceof HttpException){             //HTTP错误
            HttpException httpException = (HttpException) e;
            ex = new RetrofitException(e,httpException.code());
            switch(httpException.code()){
                case FORBIDDEN:
                    onPermissionError(ex);          //权限错误，需要实现
                    break;
                case UNAUTHORIZED:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    onError(ex);
                    break;
            }
        } else if (e instanceof ApiException){    //服务器返回的错误
            ApiException apiException = (ApiException) e;
            onResultError(apiException);
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException
                || e instanceof MalformedJsonException){
            ex = new RetrofitException(e, RetrofitException.PARSE_ERROR);
            ex.setDisplayMessage(parseMsg);            //
            onError(ex);
        } else {
            ex = new RetrofitException(e, RetrofitException.UNKNOWN);
            ex.setDisplayMessage(unknownMsg);          //未知错误
            onError(ex);
        }
    }


    /**
     * 错误回调
     */
    protected abstract void onError(RetrofitException ex);

    /**
     * 权限错误，需要实现重新登录操作
     */
    protected void onPermissionError(RetrofitException ex){

    };

    /**
     * 服务器返回的错误
     */
    protected abstract void onResultError(ApiException ex);

    @Override
    public void onCompleted() {

    }
}
