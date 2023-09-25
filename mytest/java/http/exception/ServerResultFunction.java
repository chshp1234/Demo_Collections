package com.example.aidltest.http.exception;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.http.BaseBean;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * @author csp
 * @date 2017/9/22
 */
public class ServerResultFunction implements Function<BaseBean, Object> {

    @Override
    public Object apply(@NonNull BaseBean response) throws Exception {
        // 打印服务器回传结果
        LogUtils.d(response.toString());
        if (response.getReturnValue() != 0) {
            throw new ServerException(response.getReturnValue(), response.getErrMsg());
        }
        return response.getData();
    }
}
