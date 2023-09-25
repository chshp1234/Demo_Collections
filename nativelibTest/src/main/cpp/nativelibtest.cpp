#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGOPEN 1 //日志开关，1为开，其它为关
#define LOG_TAG "JNI_LOG"
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN,    LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR,   LOG_TAG, __VA_ARGS__))

#if (LOGOPEN == 1)
#define LOGV(...) ((void)__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG,   LOG_TAG, __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO,    LOG_TAG, __VA_ARGS__))
#else
#define LOGV(...)
#define LOGD(...)
#define LOGI(...)
#endif


using namespace std;

JavaVM *javaVM;

string currentTime() {
    time_t t = time(nullptr);
    struct tm *now = localtime(&t);
    char str[50];
    strftime(str, sizeof(str), "%Y年%m月%d日 %H:%M:%S", now);
    return str;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_csp_nativelibtest_NativeLib_stringFromJNI__(
        JNIEnv *env,
        jobject obj) {
    string hello = "Hello from C++";
    jstring res = env->NewStringUTF((hello + "\n" + currentTime()).c_str());
    const char *str = env->GetStringUTFChars(res, JNI_FALSE);
    LOGI("%s", str);
    env->ReleaseStringUTFChars(res, str);
    return res;
}

//方法重载
//如果需要区分两个重载的方法，需要用双下划线__拼接上参数签名。
//方法字符串末尾需要拼接参数签名
//_转换成_1
//;转换成_2
//[转换成_3
extern "C" JNIEXPORT jstring JNICALL
Java_com_csp_nativelibtest_NativeLib_stringFromJNI__Ljava_lang_String_2(JNIEnv *env, jobject obj,
                                                                        jstring msg) {
    string hello = "Hello from C++";
    jstring res = env->NewStringUTF((hello + "\n" + currentTime()).c_str());
    const char *str = env->GetStringUTFChars(res, JNI_FALSE);
    LOGI("%s", str);
    env->ReleaseStringUTFChars(res, str);
    return res;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_csp_nativelibtest_NativeLibKt_stringFromJNI(
        JNIEnv *env,
        jclass obj) {
    string hello = "Hello from C++";
    jstring res = env->NewStringUTF((hello + "\n" + currentTime()).c_str());
    const char *str = env->GetStringUTFChars(res, JNI_FALSE);
    LOGI("%s", str);
    env->ReleaseStringUTFChars(res, str);
    return res;
}

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    javaVM = vm;
    LOGI("jni加载");
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM *vm, void *reserved) {
    javaVM = nullptr;
    LOGI("jni卸载");
}
