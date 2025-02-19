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

    jclass ency =
            findClass(env, "cxy.fun.obfuscate.utils.ByteCodeEncryption");


    jmethodID transform = (*env)->GetStaticMethodID(env,ency, "decrypt",
                                                    "([B)[B");
    if(!transform){
        MessageBoxA(NULL,"ByteCodeEncryption was null","Fit",0);
        return NULL;
    }

    jbyteArray oldBytes = (*env)->NewByteArray(env,len);
    (*env)->
            SetByteArrayRegion(env,oldBytes,
                               0, len, (jbyte *)buf);

    //if(transform)

    jbyteArray newBytes = (jbyteArray) ((*env)->CallStaticObjectMethod(env,ency, transform,oldBytes));
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


