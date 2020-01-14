// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
#include <jni.h>
#include "CryptoContext.h"

using namespace std;

extern "C" JNIEXPORT jlong JNICALL Java_com_example_groupchat_JniUtils_nativeCreateCryptoContext(JNIEnv *env, jclass jclass, jstring fileStorageDirectory, jint polyModulus, jint scale)
{
    const char *directory = env->GetStringUTFChars(fileStorageDirectory, nullptr);
    CryptoContext *context = createCryptoContext(directory, polyModulus, scale);
    env->ReleaseStringUTFChars(fileStorageDirectory, directory);
    return reinterpret_cast<long>(context);
}

extern "C" JNIEXPORT void JNICALL Java_com_example_groupchat_JniUtils_nativeReleaseCryptoContext(JNIEnv *env, jclass jclass, jlong contextHandle)
{
    releaseCryptoContext(reinterpret_cast<CryptoContext*>(contextHandle));
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_groupchat_JniUtils_nativeEncrypt(JNIEnv *env, jclass jclass, jlong contextHandle, jdoubleArray input,jstring ciphertextPath)
{
    jsize inputLength = env->GetArrayLength(input);
    jdouble *rawArray = env->GetDoubleArrayElements(input, nullptr);
    const char *ciphertext= env->GetStringUTFChars(ciphertextPath, nullptr);

    vector<double> inputVector(rawArray, rawArray + inputLength);
    env->ReleaseDoubleArrayElements(input, rawArray, JNI_ABORT);

    CryptoContext *context = reinterpret_cast<CryptoContext*>(contextHandle);
    jstring ciphertextString = env->NewStringUTF(context->encrypt(inputVector,ciphertext).c_str());
    env->ReleaseStringUTFChars(ciphertextPath, ciphertext);
    return ciphertextString ;
}

extern "C" JNIEXPORT jdoubleArray JNICALL Java_com_example_groupchat_JniUtils_nativeDecrypt(JNIEnv *env, jclass jclass, jlong contextHandle, jstring input)
{
    const char *rawInput = env->GetStringUTFChars(input, nullptr);
    CryptoContext *context = reinterpret_cast<CryptoContext*>(contextHandle);
    vector<double> output = context->decrypt(rawInput);
    env->ReleaseStringUTFChars(input, rawInput);
    jdoubleArray javaOutput = env->NewDoubleArray(output.size());
    env->SetDoubleArrayRegion(javaOutput, 0, output.size(), output.data());
    return javaOutput;
}

extern "C" JNIEXPORT jboolean JNICALL Java_com_example_groupchat_JniUtils_nativeLoadLocalKeys(
        JNIEnv *env,
        jclass jclass,
        jlong contextHandle,
        jstring publicKeyPath,
        jstring secretKeyPath)
{
    const char *publicKey = env->GetStringUTFChars(publicKeyPath, nullptr);
    const char *secretKey = env->GetStringUTFChars(secretKeyPath, nullptr);
    CryptoContext *context = reinterpret_cast<CryptoContext*>(contextHandle);
    bool result = context->loadLocalKeys(publicKey, secretKey);
    env->ReleaseStringUTFChars(publicKeyPath, publicKey);
    env->ReleaseStringUTFChars(secretKeyPath, secretKey);
    return result == true;
}

extern "C" JNIEXPORT jstring JNICALL Java_com_example_groupchat_JniUtils_nativeGenerateKeys(
    JNIEnv *env,
    jclass jclass,
    jlong contextHandle,
    jstring publicKeyPath,
    jstring secretKeyPath,
    jstring relinearizeKeyPath)
{
    const char *publicKey = env->GetStringUTFChars(publicKeyPath, nullptr);
    const char *secretKey = env->GetStringUTFChars(secretKeyPath, nullptr);
    const char *relinearizeKey = env->GetStringUTFChars(relinearizeKeyPath, nullptr);
    CryptoContext *context = reinterpret_cast<CryptoContext*>(contextHandle);
    //context->generateKeys(publicKey, secretKey, relinearizeKey);
    jstring secretKeyString = env->NewStringUTF(context->generateKeys(publicKey, secretKey, relinearizeKey).c_str());
    env->ReleaseStringUTFChars(publicKeyPath, publicKey);
    env->ReleaseStringUTFChars(secretKeyPath, secretKey);
    env->ReleaseStringUTFChars(relinearizeKeyPath, relinearizeKey);
    return secretKeyString;
}