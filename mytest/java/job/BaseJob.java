package com.example.aidltest.job;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.job.exception.OverTimeException;
import com.example.aidltest.utils.LogFileUtils;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseJob extends Job {
    private static final String TAG = "BaseJob";
    protected long addTime;
    protected long endTime;
    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long DEFAULT_OVERTIME = 3 * ONE_MINUTE;

    public static final String TIMED_JOB = "TimerJob";
    public static final String SCRIPT_JOB = "ScriptJob";
    public static final String LINKER_JOB = "LinkerJob";
    public static final String UPLOAD_JOB = "UploadJob";

    private static volatile Thread timeThread;
    private static volatile Thread scriptThread;
    private static volatile Thread linkerThread;
    private static volatile Thread uploadThread;

    protected static final String SINGLE_GET_OR_CHECK_TIKTOK_ACCOUNT =
            "single_get_or_check_tiktok_account";
    protected static final String SINGLE_CHECK_TAO_ACCOUNT = "single_check_tao_account";
    protected static final String SINGLE_GET_TAO_ACCOUNT = "single_get_tao_account";
    protected static final String SINGLE_RETURN_HOME = "single_return_home";

    public static AtomicBoolean RUN_JOB = new AtomicBoolean(false);

    protected BaseJob(Params params) {
        super(params);
    }

    @Override
    public void onAdded() {
        LogUtils.i("onAdded: " + getClass().getSimpleName());
    }

    @Override
    public void onRun() throws Throwable {
        /*todo 用什么方式优化？*/
        switch (getRunGroupId()) {
            case TIMED_JOB:
                if (timeThread == null || timeThread != Thread.currentThread()) {
                    timeThread = Thread.currentThread();
                }
                break;
            case SCRIPT_JOB:
                if (scriptThread == null || scriptThread != Thread.currentThread()) {
                    scriptThread = Thread.currentThread();
                }
                break;
            case LINKER_JOB:
                if (linkerThread == null || linkerThread != Thread.currentThread()) {
                    linkerThread = Thread.currentThread();
                }
                break;
            case UPLOAD_JOB:
                if (uploadThread == null || uploadThread != Thread.currentThread()) {
                    uploadThread = Thread.currentThread();
                }
                break;
        }
        /*if (TIMED_JOB.equals(getRunGroupId())) {
            timeThread = Thread.currentThread();
        }
        if (SCRIPT_JOB.equals(getRunGroupId())) {
            scriptThread = Thread.currentThread();
        }
        if (LINKER_JOB.equals(getRunGroupId())) {
            linkerThread = Thread.currentThread();
        }
        if (UPLOAD_JOB.equals(getRunGroupId())) {
            uploadThread = Thread.currentThread();
        }*/
        LogUtils.i("onRun: " + getClass().getSimpleName());

        if (checkEnvironment()) {
            doJob();
        }

        // 手机重启后，可能还会去执行这些方法
        /*if ((System.currentTimeMillis() - this.addTime) > DEFAULT_OVERTIME) {
            throw new OverTimeException();
        }*/
    }

    /** 开始做任务 */
    public abstract void doJob() throws Throwable;

    /** 重写这个方法进行资源回收 */
    protected void release() {}

    /**
     * 做任务前，检测任务环境
     *
     * @return the boolean
     */
    protected boolean checkEnvironment() {
        return true;
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {
        LogUtils.i(
                "onCancel: "
                        + getClass().getSimpleName()
                        + " cancelReason "
                        + cancelReason
                        + " Throwable "
                        + throwable);
        release();
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(
            @NonNull Throwable throwable, int runCount, int maxRunCount) {
        LogUtils.e(
                "shouldReRunOnThrowable: "
                        + getClass().getSimpleName()
                        + throwable
                        + "\nreason:"
                        + ((throwable instanceof BaseScriptException)
                                ? ((BaseScriptException) throwable).getResultBean().getJobMessage()
                                : throwable.toString()));
        LogFileUtils.fileScript(
                getClass().getSimpleName(),
                ((throwable instanceof BaseScriptException)
                        ? ((BaseScriptException) throwable).getResultBean().getJobMessage()
                        : throwable.toString()));
        if (throwable instanceof OverTimeException) {
            LogUtils.e("OverTime: ", System.currentTimeMillis() - addTime);
            return RetryConstraint.CANCEL;
        }
        return RetryConstraint.CANCEL;
    }

    public static void stop() {
        RUN_JOB.getAndSet(false);
        stop(TIMED_JOB);
        stop(SCRIPT_JOB);
        stop(LINKER_JOB);
    }

    public static void stop(String jobGroup) {
        if (TIMED_JOB.equals(jobGroup)) {
            if (timeThread != null) {
                LogUtils.d("timeThread stop: " + timeThread.getName());
                timeThread.interrupt();
                timeThread = null;
            }
        } else if (SCRIPT_JOB.equals(jobGroup)) {
            if (scriptThread != null) {
                LogUtils.d("scriptThread stop: " + scriptThread.getName());
                scriptThread.interrupt();
                scriptThread = null;
            }
        } else if (LINKER_JOB.equals(jobGroup)) {
            if (linkerThread != null) {
                LogUtils.d("linkerThread stop: " + linkerThread.getName());
                linkerThread.interrupt();
                linkerThread = null;
            }
        } else if (UPLOAD_JOB.equals(jobGroup)) {

        }
    }

    public static boolean isRun() {
        return RUN_JOB.get();
    }
}
