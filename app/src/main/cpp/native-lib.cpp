#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring

JNICALL
Java_com_yibo_ymusic_MainMusicActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_yibo_ymusic_activity_PlayerActivity_baseCall(JNIEnv *env, jobject instance, jint x,
                                                      jstring y_) {
    const char *y = env->GetStringUTFChars(y_, 0);

    // TODO

    env->ReleaseStringUTFChars(y_, y);

    return env->NewStringUTF("111");
}