//package com.example.aidltest.utils;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Looper;
//
//import androidx.annotation.IntDef;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TreeSet;
//
///**
// * <pre>
// *     author: blankj
// *     blog  : http://blankj.com
// *     time  : 2019/04/09
// *     desc  : 弹窗管理
// * </pre>
// */
//public class PopManager {
//
//    private static final String SWITCH = "AUDialogWindow_AUPopManager_disable";
//    private static final String SHOW = "101056";
//    private static final String DISMISS = "101073";
//    private static final String PRE_SHOW = "101074";
//    private static final String DISMISS_ALL_POPUP = "101075";
//    private static final String QUEUE = "101076";
//    private static final String REPLACED = "101077";
//    private static final Map<String, String> ACTION_MAP = new HashMap<>();
//
//    private static final String BIZ_TYPE = "middle";
//    private static final String TAG = "PopManager";
//    private static final HashMap<Activity, TreeSet<PopBean>> DIALOGS = new HashMap<>();
//
//    static {
//        ACTION_MAP.put(SHOW, "show");
//        ACTION_MAP.put(DISMISS, "dismiss");
//        ACTION_MAP.put(PRE_SHOW, "preshow");
//        ACTION_MAP.put(DISMISS_ALL_POPUP, "dismissallpopup");
//        ACTION_MAP.put(QUEUE, "queue");
//        ACTION_MAP.put(REPLACED, "replaced");
//    }
//
//    public static void show(final AUPop pop) {
//        if (pop == null) return;
//        try {
//            if (!isOpen()) {
//                pop.showPop();
//                return;
//            }
//            final Activity activity = pop.getPopActivity();
//            if (activity == null) {
//                AuiLogger.error(TAG, "pop's context is not instance of activity");
//                return;
//            }
//            if (activity.isFinishing() || activity.isDestroyed()) {
//                AuiLogger.info(TAG, "Activity is finish, name = " + activity.getClass().getName());
//                DIALOGS.remove(activity);
//                return;
//            }
//
//            PopBean popBean = findPop(pop);
//            if (popBean == null) {
//                popBean = new PopBean(pop, System.currentTimeMillis(), System.nanoTime());
//            } else {
//                popBean.showMillis = System.currentTimeMillis();
//                popBean.showNano = System.nanoTime();
//            }
//            AuiLogger.info(TAG, "start show");
//            trace(activity, popBean, PRE_SHOW);
//            TreeSet<PopBean> popBeans = DIALOGS.get(activity);
//            if (popBeans == null) {
//                popBeans = new TreeSet<>();
//                DIALOGS.put(activity, popBeans);
//            }
//            popBean.isRegister = true;
//            popBeans.add(popBean);
//            PopBean first = popBeans.first();
//            if (first == null) {
//                popBeans.pollFirst();
//                first = popBeans.first();
//            }
//            if (first.isShowing) {// 优先级最高的处于展示状态
//                trace(activity, popBean, QUEUE);
//                popBean.isQueue = true;
//            } else {// 否则优先级最高的显示
//                show(first);
//                if (popBeans.size() == 1) {// 如果弹窗只有一个那就说明是简单的 show
//                    trace(activity, first, SHOW);
//                } else {// 抢占
//                    trace(activity, first, REPLACED);
//                }
//            }
//        } catch (Throwable t) {
//            AuiLogger.error(TAG, t.toString());
//        }
//    }
//
//    public static void dismiss(final AUPop pop) {
//        if (pop == null) return;
//        try {
//            if (!isOpen()) {
//                pop.dismissPop();
//                return;
//            }
//            Activity activity = pop.getPopActivity();
//            if (activity == null) {
//                AuiLogger.error(TAG, "pop's context is not instance of activity");
//                return;
//            }
//            if (activity.isFinishing() || activity.isDestroyed()) {
//                AuiLogger.info(TAG, "Activity is finish, name = " + activity.getClass().getName());
//                DIALOGS.remove(activity);
//                return;
//            }
//            PopBean popBean = findPop(pop);
//            if (popBean == null) return;
//            AuiLogger.info(TAG, "start dismiss");
//            TreeSet<PopBean> dialogs = DIALOGS.get(activity);
//            if (dialogs == null || dialogs.isEmpty()) return;
//            if (dialogs.contains(popBean)) {
//                dialogs.remove(popBean);
//                popBean.isRegister = false;
//                popBean.mAUPop.dismissPop();
//                trace(activity, popBean, DISMISS);
//            }
//            if (dialogs.isEmpty()) return;
//            PopBean first = dialogs.first();
//            if (first == null) return;
//            if (!first.isShowing) {// 把优先级最高的显示出来
//                show(first);
//                trace(activity, first, SHOW);
//            }
//        } catch (Throwable t) {
//            AuiLogger.error(TAG, t.toString());
//        }
//    }
//
//    private static void show(final PopBean bean) {
//        bean.isQueue = false;
//        bean.mAUPop.showPop();
//        bean.isShowing = true;
//    }
//
//    protected static PopBean findPop(final AUPop pop) {
//        TreeSet<PopBean> popBeans = DIALOGS.get(pop.getPopActivity());
//        if (popBeans == null) return null;
//        for (PopBean popBean : popBeans) {
//            if (popBean.mAUPop == pop) {
//                return popBean;
//            }
//        }
//        return null;
//    }
//
//    public static void dismissAll(final Activity activity) {
//        if (!isOpen()) return;
//        if (activity == null) return;
//        try {
//            TreeSet<PopBean> popBeans = DIALOGS.get(activity);
//            if (popBeans == null || popBeans.isEmpty()) return;
//            for (PopBean popBean : popBeans) {
//                if (popBean != null) popBean.mAUPop.dismissPop();
//            }
//            DIALOGS.remove(activity);
//            HashMap<String, String> ext = new HashMap<>();
//            ext.put("action", ACTION_MAP.get(DISMISS_ALL_POPUP));
//            ext.put("referer_url", activity.getClass().getName());
//            AuiLogger.trace(DISMISS_ALL_POPUP, BIZ_TYPE, ext);
//        } catch (Throwable t) {
//            AuiLogger.error(TAG, t.toString());
//        }
//    }
//
//    private static void trace(Activity activity, PopBean pop, String eventID) {
//        AuiLogger.info(TAG, ACTION_MAP.get(eventID) + ": " + pop);
//        HashMap<String, String> ext = new HashMap<>();
//        ext.put("action", ACTION_MAP.get(eventID));
//        ext.put("content_type", pop.mAUPop.getClass().getName());
//        ext.put("referer_url", activity.getClass().getName());
//        ext.put("category", getBaseDialogName(pop.mAUPop));
//        AuiLogger.trace(eventID, BIZ_TYPE, ext);
//    }
//
//    private static String getBaseDialogName(AUPop pop) {
//        Class temp = pop.getClass();
//        String ret = "";
//        for (int i = 0; i < 5; i++) {
//            if (temp == null) return ret;
//            ret = temp.getSimpleName();
//            if (ret.startsWith("AP") || ret.startsWith("AU") || ret.startsWith("Bee")) {
//                return ret;
//            }
//            temp = temp.getSuperclass();
//        }
//        return ret;
//    }
//
//    /**
//     * 是否开启弹窗管控
//     *
//     * @return 读取开关，默认开启
//     */
//    private static boolean isOpen() {
//        ConfigExecutor executor = AntUIExecutorManager.getInstance().getConfigExecutor();
//        if (executor != null) {
//            try {
//                return !Boolean.parseBoolean(executor.getConfig(SWITCH));
//            } catch (Exception e) {
//                AuiLogger.error(TAG, "ConfigService 配置错误: " + e);
//            }
//        }
//        return true;
//    }
//
//    static class PopBean implements Comparable<PopBean> {
//        AUPop mAUPop;
//        long showMillis;
//        long showNano;
//        boolean isRegister;
//        boolean isShowing;
//        boolean isQueue;
//
//        public PopBean(AUPop auPop, long showMillis, long showNano) {
//            mAUPop = auPop;
//            this.showMillis = showMillis;
//            this.showNano = showNano;
//        }
//
//        @Override
//        public int compareTo(@NonNull PopBean o) {
//            int i = o.mAUPop.getPriority() - mAUPop.getPriority();
//            if (i != 0) return i;
//            int i1 = (int) (showMillis - o.showMillis);
//            if (i1 != 0) return i1;
//            return (int) (showNano - o.showNano);
//        }
//
//        @Override
//        public String toString() {
//            return "PopBean { priority: " + mAUPop.getPriority() + ", showMillis: " + showMillis + " }";
//        }
//    }
//}
//
//public class AUBasicDialog extends Dialog
//        implements AUPop {
//
//    private static final String TAG = "AUBasicDialog";
//    private static final String ACTION = "com.alipay.mobile.antui.basic.AUBasicDialog";
//
//    public static final int PRIORITY_UNDEFINED = 0;
//    public static final int PRIORITY_BELOW_CDP = 40;
//    public static final int PRIORITY_CDP = 50;
//    public static final int PRIORITY_BETWEEN_CDP_SHARE_TOKEN = 55;
//    public static final int PRIORITY_SHARE_TOKEN = 60;
//    public static final int PRIORITY_BIZ = 100;
//
//    @IntDef({PRIORITY_UNDEFINED, PRIORITY_BELOW_CDP, PRIORITY_CDP,
//            PRIORITY_BETWEEN_CDP_SHARE_TOKEN, PRIORITY_SHARE_TOKEN, PRIORITY_BIZ})
//    @Retention(RetentionPolicy.SOURCE)
//    public @interface Priority {
//    }
//
//    protected Context mContext;
//    private int priority = PRIORITY_BIZ;
//
//    public AUBasicDialog(@NonNull Context context) {
//        super(context);
//        mContext = context;
//    }
//
//    public AUBasicDialog(@NonNull Context context, int themeResId) {
//        super(context, themeResId);
//        mContext = context;
//    }
//
//    protected AUBasicDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
//        super(context, cancelable, cancelListener);
//        mContext = context;
//    }
//
//    public void show(int priority) {
//        this.priority = priority;
//        show();
//    }
//
//    @Override
//    public void show() {
//        if (!isLiving()) {
//            AuiLogger.info(TAG, "Activity is finish");
//            return;
//        }
//        PopManager.PopBean pop = PopManager.findPop(this);
//        if (pop != null && pop.isRegister){
//            if (!pop.isQueue) {
//                AuiLogger.info(TAG, "show:" + mContext);
//                super.show();
//            }
//            return;
//        }
//        PopManager.show(this);
//    }
//
//    @Override
//    public void dismiss() {
//        PopManager.dismiss(this);
//    }
//
//    @Override
//    public void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        PopManager.dismiss(this);
//    }
//
//    private boolean isLiving() {
//        if (mContext == null) {
//            return false;
//        }
//        Activity activity = ViewUtils.getActivityByContext(mContext);
//        if (activity == null) {
//            return false;
//        }
//        if (activity.isFinishing() || activity.isDestroyed()) {
//            //show弹框有问题时，增加Activity的classname日志
//            AuiLogger.info("AUDialog", "Activity is finish,name=" + activity.getClass().getName());
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(ACTION));
//    }
//
//    public void setPriority(int priority) {
//        this.priority = priority;
//    }
//
//    @Override
//    public Activity getPopActivity() {
//        return ViewUtils.getActivityByContext(getContext());
//    }
//
//    @Override
//    public void showPop() {
//        show();
//    }
//
//    @Override
//    public int getPriority() {
//        return priority;
//    }
//
//    @Override
//    public void dismissPop() {
//        //增加线程判断 如果在主线程，用try catch 捕获，如果不在主线程 return掉， 解决弹窗页面压后台返回无法消失的问题
//        if (Looper.myLooper() != Looper.getMainLooper() && !isLiving()) {
//            AuiLogger.info(TAG, "Activity is finish");
//            return;
//        }
//        if (this.isShowing()) { //check if dialog is showing.
//            try {
//                AuiLogger.info(TAG, "isShowing() == true, dismiss");
//                super.dismiss();
//            } catch (IllegalArgumentException e) {
//                AuiLogger.error(TAG, "IllegalArgumentException: e" + e);
//            }
//        }
//    }
//}
//
