package com.example.aidltest.http.exception;

import android.util.MalformedJsonException;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.text.ParseException;

import retrofit2.HttpException;

/**
 * 异常处理类
 *
 * @author csp
 * @date 2017/9/22
 */
public class ExceptionEngine {
    /** 未知错误 */
    public static final int UNKNOWN_ERROR = 1000;
    /** 解析(服务器)数据错误 */
    public static final int ANALYTIC_SERVER_DATA_ERROR = 1001;
    /** 解析(客户端)数据错误 */
    public static final int ANALYTIC_CLIENT_DATA_ERROR = 1002;
    /** 网络连接错误 */
    public static final int CONNECT_ERROR = 1003;
    /** 网络连接超时 */
    public static final int TIME_OUT_ERROR = 1004;
    /** 服务器异常 */
    public static final int UNKNOWN_HOST_ERROR = 1005;
    /** 无网络 */
    public static final int NO_CONNECT = 1006;

    public static ApiException handleException(Throwable e) {
        ApiException ex;

        if (!NetworkUtils.isConnected()) {
            ex = new ApiException(e, NO_CONNECT);
            ex.setMsg("无网络连接:" + e.getClass().getSimpleName());
            ToastUtils.showShort("网络不给力，请检查网络设置");
            return ex;
        }
        // 均视为网络错误
        else if (e instanceof HttpException) {
            HttpException httpExc = (HttpException) e;
            ex = new ApiException(e, httpExc.code());
            ex.setMsg("网络错误:" + e.getClass().getSimpleName());
            return ex;
        }
        // 服务器返回的错误
        else if (e instanceof ServerException) {
            ServerException serverExc = (ServerException) e;
            ex = new ApiException(serverExc, serverExc.getCode());
            ex.setMsg(serverExc.getMsg());
            return ex;
        }
        // 解析数据错误
        else if (e instanceof JsonSyntaxException
                || e instanceof JsonIOException
                || e instanceof JsonParseException
                || e instanceof ParseException
                || e instanceof MalformedJsonException) {
            ex = new ApiException(e, ANALYTIC_SERVER_DATA_ERROR);
            ex.setMsg("解析错误:" + e.getClass().getSimpleName());
            return ex;
        }
        // 连接网络错误
        else if (e instanceof ConnectException) {
            ex = new ApiException(e, CONNECT_ERROR);
            ex.setMsg("连接失败:" + e.getClass().getSimpleName());
            return ex;

        }
        // 网络超时
        else if (e instanceof SocketTimeoutException) {
            ex = new ApiException(e, TIME_OUT_ERROR);
            ex.setMsg("网络超时:" + e.getClass().getSimpleName());
            ToastUtils.showShort("当前网络较差，请稍后重试");
            return ex;
        }
        // 服务器异常
        else if (e instanceof UnknownHostException || e instanceof UnknownServiceException) {
            ex = new ApiException(e, UNKNOWN_HOST_ERROR);
            ex.setMsg("服务器异常:" + e.getClass().getSimpleName());
            return ex;
        }
        // 未知错误
        else {
            ex = new ApiException(e, UNKNOWN_ERROR);
            ex.setMsg("未知错误:" + e.getClass().getSimpleName());
            return ex;
        }
    }
}
