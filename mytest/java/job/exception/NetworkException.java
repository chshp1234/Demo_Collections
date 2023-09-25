package com.example.aidltest.job.exception;


import com.example.aidltest.job.BaseScriptException;
import com.example.aidltest.job.JobResultBean;

public class NetworkException extends BaseScriptException {
    public NetworkException(JobResultBean bean) {
        super(bean, JobExceptionConfig.NETWORK_EXCEPTION, "网络连接超时");
    }
}
