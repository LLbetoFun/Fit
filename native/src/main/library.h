#ifndef NATIVE_LIBRARY_H
#define NATIVE_LIBRARY_H
#include "../java/jni.h"
void hello(void);

#endif //NATIVE_LIBRARY_H

#ifndef _Included_cxy_fun_obfuscate_utils_FitLoader
#define _Included_cxy_fun_obfuscate_utils_FitLoader
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cxy_fun_obfuscate_utils_FitLoader
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cxy_fun_obfuscate_utils_FitLoader_init
(JNIEnv *, jclass);
typedef jclass(*JVM_DefineClass)(JNIEnv *env, const char *name, jobject loader, const jbyte *buf,
                                 jsize len, jobject pd);
typedef jclass(*JVM_DefineClassWithSource)(JNIEnv *env, const char *name, jobject loader,
                                           const jbyte *buf, jsize len, jobject pd,
                                           const char *source);
#ifdef __cplusplus
}
#endif
#endif
