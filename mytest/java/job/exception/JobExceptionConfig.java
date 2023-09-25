package com.example.aidltest.job.exception;

public interface JobExceptionConfig {
    // 添加购物车失败
    int ADD_SHOPPING_CART_FAILED_EXCEPTION = 100;
    // 收藏商品失败
    int COLLECT_GOODS_FAILED_EXCEPTION = 101;
    // 商品未找到
    int GOODS_NOTE_FOUND_EXCEPTION = 102;
    // 控件未找到
    int NODE_NOTE_FOUND_EXCEPTION = 103;
    // 网络异常
    int NETWORK_EXCEPTION = 104;
    // 返回淘宝首页失败
    int RETURN_TAO_MAIN_FAIL = 105;
    // 用户主动退出
    int USER_STOP_TASK = 106;
    // 异常列表（老的查找方式中，没有“更多”按钮的商品）
    int STRANGE_SEARCH_LIST_EXCEPTION = 107;

    // 返回抖音首页失败
    int RETURN_TIKTOK_MAIN_FAIL = 205;
    // 获取抖音版本控件失败
    int CHECK_TIKTOK_VERSION_FAIL = 206;

    // 其他异常
    int OTHER_EXCEPTION = 1000;
}
