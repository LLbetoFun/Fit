#include "library.h"
#include "../java/jni.h"
#include "../java/jvmti.h"
#include "../hooker/hook.h"

#include <stdio.h>
#include <windows.h>

typedef struct {
    JavaVM *vm;
    JNIEnv *jniEnv;
    jvmtiEnv *jvmtiEnv;
} JAVA;

static JVM_DefineClass defineClass;
static JVM_DefineClassWithSource defineClass2;


JNIEXPORT jbyteArray JNICALL decrypt(JNIEnv *env, jbyteArray combinedData) {
    // 检查 combinedData 是否以 0xCA 0xFE 开头
    jbyte *combinedDataBytes = (*env)->GetByteArrayElements(env, combinedData, NULL);
    if (combinedDataBytes[0] == (jbyte)0xCA && combinedDataBytes[1] == (jbyte)0xFE) {
        (*env)->ReleaseByteArrayElements(env, combinedData, combinedDataBytes, JNI_ABORT);
        return NULL; // 返回 NULL 表示解密失败
    }

    // 提取加密数据和密钥
    int keySize = 16; // AES 128 位密钥的字节长度
    jsize combinedDataLength = (*env)->GetArrayLength(env, combinedData);
    jsize encryptedDataLength = combinedDataLength - keySize;

    // 创建 Java 字节数组
    jbyteArray encryptedData = (*env)->NewByteArray(env, encryptedDataLength);
    jbyteArray keyBytes = (*env)->NewByteArray(env, keySize);

    // 复制数据到 Java 字节数组
    (*env)->SetByteArrayRegion(env, encryptedData, 0, encryptedDataLength, combinedDataBytes);
    (*env)->SetByteArrayRegion(env, keyBytes, 0, keySize, combinedDataBytes + encryptedDataLength);

    // 释放 combinedData
    (*env)->ReleaseByteArrayElements(env, combinedData, combinedDataBytes, JNI_ABORT);

    // 获取 javax.crypto.Cipher 类
    jclass cipherClass = (*env)->FindClass(env, "javax/crypto/Cipher");
    if (cipherClass == NULL) {
        return NULL; // 类未找到
    }

    // 获取 Cipher.getInstance 方法
    jmethodID getInstanceMethod = (*env)->GetStaticMethodID(env, cipherClass, "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
    if (getInstanceMethod == NULL) {
        return NULL; // 方法未找到
    }

    // 调用 Cipher.getInstance("AES")
    jstring algorithm = (*env)->NewStringUTF(env, "AES");
    jobject cipher = (*env)->CallStaticObjectMethod(env, cipherClass, getInstanceMethod, algorithm);
    (*env)->DeleteLocalRef(env, algorithm);

    // 获取 SecretKeySpec 类
    jclass secretKeySpecClass = (*env)->FindClass(env, "javax/crypto/spec/SecretKeySpec");
    if (secretKeySpecClass == NULL) {
        return NULL; // 类未找到
    }

    // 获取 SecretKeySpec 构造函数
    jmethodID secretKeySpecConstructor = (*env)->GetMethodID(env, secretKeySpecClass, "<init>", "([BLjava/lang/String;)V");
    if (secretKeySpecConstructor == NULL) {
        return NULL; // 构造函数未找到
    }

    // 创建 SecretKeySpec 对象
    jstring keyAlgorithm = (*env)->NewStringUTF(env, "AES");
    jobject secretKey = (*env)->NewObject(env, secretKeySpecClass, secretKeySpecConstructor, keyBytes, keyAlgorithm);
    (*env)->DeleteLocalRef(env, keyAlgorithm);

    // 获取 Cipher.init 方法
    jmethodID initMethod = (*env)->GetMethodID(env, cipherClass, "init", "(ILjava/security/Key;)V");
    if (initMethod == NULL) {
        return NULL; // 方法未找到
    }

    // 调用 Cipher.init(Cipher.DECRYPT_MODE, key)
    jint decryptMode = 2; // Cipher.DECRYPT_MODE 的值是 2
    (*env)->CallVoidMethod(env, cipher, initMethod, decryptMode, secretKey);

    // 获取 Cipher.doFinal 方法
    jmethodID doFinalMethod = (*env)->GetMethodID(env, cipherClass, "doFinal", "([B)[B");
    if (doFinalMethod == NULL) {
        return NULL; // 方法未找到
    }

    // 调用 Cipher.doFinal(encryptedData)
    jbyteArray decryptedData = (jbyteArray)(*env)->CallObjectMethod(env, cipher, doFinalMethod, encryptedData);

    // 返回解密后的数据
    return decryptedData;
}


jclass JNICALL findClass0(JNIEnv *jniEnv, const char *name, jobject classloader) {

    jmethodID loadClass = (*jniEnv)->GetMethodID(jniEnv, (*jniEnv)->GetObjectClass(jniEnv,classloader), "loadClass",
                                                 "(Ljava/lang/String;)Ljava/lang/Class;");

    return (jclass) (*jniEnv)->CallObjectMethod(jniEnv, classloader, loadClass,
                                                (*jniEnv)->NewStringUTF(jniEnv, name));
}

extern JNIEXPORT jclass JNICALL findClass(JNIEnv *jniEnv, const char *name) {
    //MessageBoxA(NULL, newName, "FishCient", 0);
    jclass ClassLoader = (*jniEnv)->FindClass(jniEnv, "java/lang/ClassLoader");
    jmethodID getSystemClassLoader = (*jniEnv)->GetStaticMethodID(jniEnv, ClassLoader, "getSystemClassLoader",
                                                                  "()Ljava/lang/ClassLoader;");

    jobject classloader;
    classloader = (*jniEnv)->CallStaticObjectMethod(jniEnv, ClassLoader, getSystemClassLoader);
    return findClass0(jniEnv,name,classloader);
}


extern JNIEXPORT void JNICALL classFileLoadHook(jvmtiEnv * jvmti_env, JNIEnv * env,
                                                jclass
                                                class_being_redefined,
                                                jobject loader,
                                                const char *name, jobject
                                                protection_domain,
                                                jint class_data_len,
                                                const unsigned char *class_data,
                                                jint
                                                *new_class_data_len,
                                                unsigned char **new_class_data
)
{

    jclass ency =
            findClass(env, "cxy.fun.obfuscate.utils.ByteCodeEncryption");

    if(!ency){
        MessageBoxA(NULL,"NativeUtils was null","Fit",0);
        return;
    }
    jmethodID transform = (*env)->GetStaticMethodID(env,ency, "decrypt",
                                                    "decrypt([B)[B");


    jbyteArray oldBytes = (*env)->NewByteArray(env,class_data_len);
    (*env)->
            SetByteArrayRegion(env,oldBytes,
                               0, class_data_len, (jbyte *)class_data);


    jbyteArray newBytes = (jbyteArray) ((*env)->CallStaticObjectMethod(env,ency, transform,oldBytes));

    jsize size = (*env)->GetArrayLength(env,newBytes);
    jbyte *classByteArray = (*env)->GetByteArrayElements(env,newBytes, NULL);
    *
            new_class_data = (unsigned char *) classByteArray;
    *
            new_class_data_len = size;


//    env->ReleaseByteArrayElements(newBytes,classByteArray,0);


}


JNIEXPORT jclass JNICALL
Hook_DefineClass(JNIEnv *env, const char *name, jobject loader, const jbyte *buf,
                jsize len, jobject pd){

    jclass ency =
            findClass(env, "cxy.fun.obfuscate.utils.ByteCodeEncryption");

    if(!ency){
        MessageBoxA(NULL,"NativeUtils was null","Fit",0);
        return NULL;
    }
    jmethodID transform = (*env)->GetStaticMethodID(env,ency, "decrypt",
                                                    "decrypt([B)[B");


    jbyteArray oldBytes = (*env)->NewByteArray(env,len);
    (*env)->
            SetByteArrayRegion(env,oldBytes,
                               0, len, (jbyte *)buf);


    jbyteArray newBytes = (jbyteArray) ((*env)->CallStaticObjectMethod(env,ency, transform,oldBytes));

    jsize size = (*env)->GetArrayLength(env,newBytes);
    jbyte *classByteArray = (*env)->GetByteArrayElements(env,newBytes, NULL);


    UnHookFunctionAdress64(defineClass);
    jclass result= defineClass(env,name,loader,classByteArray,size,pd);
    HookFunctionAdress64(defineClass,(Hook_DefineClass));

    return result;
}
JNIEXPORT jclass JNICALL
Hook_DefineClass2(JNIEnv *env, const char *name, jobject loader,
                  const jbyte *buf, jsize len, jobject pd,
                  const char *source){

    if(loader==NULL||buf==NULL){
        //UnHookFunctionAdress64(defineClass2);
        //MessageBoxA(NULL,name,"Fit",0);

        jclass c= (*env)->DefineClass(env,name,loader,buf,len);

        //HookFunctionAdress64(defineClass2, Hook_DefineClass2);
        return c;
    }

    /*jclass ency =
            findClass(env, "cxy.fun.obfuscate.utils.ByteCodeEncryption");


    jmethodID transform = (*env)->GetStaticMethodID(env,ency, "decrypt",
                                                    "([B)[B");
    if(!transform){
        MessageBoxA(NULL,"ByteCodeEncryption was null","Fit",0);
        return NULL;
    }
*/
    jbyteArray oldBytes = (*env)->NewByteArray(env,len);
    (*env)->
            SetByteArrayRegion(env,oldBytes,
                               0, len, (jbyte *)buf);

    //if(transform)

    jbyteArray newBytes = decrypt(env,oldBytes);
    if(newBytes==NULL){
        UnHookFunctionAdress64(defineClass2);
        jclass c= defineClass2(env,name,loader,buf,len,pd,source);
        HookFunctionAdress64(defineClass2, Hook_DefineClass2);
        return c;
    }

    jsize size = (*env)->GetArrayLength(env,newBytes);
    jbyte *classByteArray = (*env)->GetByteArrayElements(env,newBytes, NULL);


    //MessageBoxA(NULL,name,"Fit",0);

    return (*env)->DefineClass(env,name,loader,classByteArray,size);
}

extern JNIEXPORT void JNICALL *allocate(jlong size) {
    void *resultBuffer = malloc(size);
    return resultBuffer;
}
extern JAVA JNICALL GetJAVA(JNIEnv *env){
    JAVA java;
    java.jniEnv=env;
    HMODULE hJvm = GetModuleHandle("jvm.dll");

    typedef jint(JNICALL *fnJNI_GetCreatedJavaVMs)(JavaVM **, jsize, jsize *);
    fnJNI_GetCreatedJavaVMs JNI_GetCreatedJavaVMs;
    JNI_GetCreatedJavaVMs = (fnJNI_GetCreatedJavaVMs) GetProcAddress(hJvm,
                                                                     "JNI_GetCreatedJavaVMs");

    jint num = JNI_GetCreatedJavaVMs(&java.vm, 1, NULL);

    jint num1=(*java.vm)->GetEnv(java.vm, (void **) (&java.jvmtiEnv),JVMTI_VERSION);
    char *errc = (char *) allocate(4);

    if(!java.vm)MessageBoxA(NULL, itoa(num,errc,10),"FishCient",0);
    if(!java.jvmtiEnv)MessageBoxA(NULL,itoa(num1,errc,10),"FishCient",0);
    return java;

}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved){
    return 0;
}

JNIEXPORT void JNICALL Java_cxy_fun_obfuscate_utils_FitLoader_init
        (JNIEnv * env, jclass fitLoader){
    JAVA Java=(GetJAVA(env));

    jvmtiCapabilities capabilities;
    memset(&capabilities, 0, sizeof(jvmtiCapabilities));


    //MessageBoxA(NULL,"Loaded","Fit",0);
    capabilities.can_get_bytecodes = 1;
    capabilities.can_redefine_classes = 1;
    capabilities.can_redefine_any_class = 1;
    capabilities.can_generate_all_class_hook_events = 1;
    capabilities.can_retransform_classes = 1;
    capabilities.can_retransform_any_class = 1;

    (*Java.jvmtiEnv)->AddCapabilities(Java.jvmtiEnv,&capabilities);

    jvmtiEventCallbacks callbacks;
    memset(&callbacks, 0, sizeof(jvmtiEventCallbacks));
    //callbacks.ClassFileLoadHook = &classFileLoadHook;
    HMODULE jvm = GetModuleHandle("jvm.dll");

    defineClass=(JVM_DefineClass)GetProcAddress(jvm, "JVM_DefineClass");

    HookFunctionAdress64(defineClass,(Hook_DefineClass));

    defineClass=(JVM_DefineClass )GetProcAddress(jvm, "JVM_DefineClass");
    defineClass2=(JVM_DefineClassWithSource)GetProcAddress(jvm, "JVM_DefineClassWithSource");

    //HookFunctionAdress64(defineClass,(Hook_DefineClass));
    HookFunctionAdress64(defineClass2,(Hook_DefineClass2));

    //(*Java.jvmtiEnv)->SetEventCallbacks(Java.jvmtiEnv,&callbacks, sizeof(jvmtiEventCallbacks));

}


