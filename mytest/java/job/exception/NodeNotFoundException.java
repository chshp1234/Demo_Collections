package com.example.aidltest.job.exception;


import com.example.aidltest.job.BaseScriptException;
import com.example.aidltest.job.JobResultBean;

public class NodeNotFoundException extends BaseScriptException {
    public NodeNotFoundException(JobResultBean bean) {
        super(bean, JobExceptionConfig.NODE_NOTE_FOUND_EXCEPTION, "查找控件失败");
    }
}
