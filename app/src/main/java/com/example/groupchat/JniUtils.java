package com.example.groupchat;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.CPABEMHOOAddress;

public class JniUtils {
    private static final String PUBLIC_KEY = "publicKeyAS.txt";
    private static final String SECRET_KEY = "secretKeyAS.txt";
    private static final String RELINEARIZATION_KEY = "relinearizeKeyAS.txt";
    private static String mFileStorageDirectory;

    static {
        System.loadLibrary("seal");//第三方so库
        System.loadLibrary("cryptoadapter");//jni规范的so库
        System.loadLibrary("jpeg");//第三方so库
        System.loadLibrary("native-lib");//实现Scrambling的so库

    }
    /*************************************************************Scrambling libjpeg-Scrambling部分的接口*********************************************************/
    public static native String scramblingBitmap(String srcFilePath, int[][] faceDatas, String outFilePath);

    /*****************************************************************CKKS 同态加密部分的接口*********************************************************************/

    /**
     * The native code handle used to encrypt and decrypt data
     */
    private static long mCryptoContext;

    //创建CryptoContext
    public static long createCryptoContext(String fileStorageDirectory, int polyModulus, int scale) throws Exception {
        mFileStorageDirectory = fileStorageDirectory + "/";
        mCryptoContext = nativeCreateCryptoContext(mFileStorageDirectory, polyModulus, scale);
        Log.e("error", "CryptoContext:"+mCryptoContext );
        return mCryptoContext;
    }
    //释放CryptoContext
    public static void releaseCryptoContext() {
        nativeReleaseCryptoContext(mCryptoContext);
    }

    //生成密钥
    public static void keyGen(String fileStorageDirectory){
        mFileStorageDirectory = fileStorageDirectory + "/";
         String secretKeyString = nativeGenerateKeys(
                mCryptoContext,
                mFileStorageDirectory + PUBLIC_KEY,
                mFileStorageDirectory + SECRET_KEY,
                mFileStorageDirectory + RELINEARIZATION_KEY);
    }

    //加载本地密钥
    public static boolean loadLocalKeys(String fileStorageDirectory){
        mFileStorageDirectory = fileStorageDirectory + "/";
        return nativeLoadLocalKeys(mCryptoContext, mFileStorageDirectory + PUBLIC_KEY, mFileStorageDirectory + SECRET_KEY);
    }

    //加密
    public static String encrypt(double[] values,String ciphertextPath) {
//        String ciphertextString = nativeEncrypt(mCryptoContext, values, ciphertextPath);
//        FileWriter writer;
//        try {
//            writer = new FileWriter(CPABEMHOOAddress.CKKSAddress + File.separator +"ciphertextString.txt");
//            writer.write(ciphertextString);
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return nativeEncrypt(mCryptoContext, values, ciphertextPath);

    }
    //解密
    public static double[] decrypt(String base64Input) {
        return nativeDecrypt(mCryptoContext, base64Input);
    }

    //////////////////////////////////////////////////Native 方法//////////////////////////////////////////////////////////////

    /**
     * A native method that is implemented by the 'cryptoadapter' native library,
     * which is packaged with this application.
     */
    public native static long nativeCreateCryptoContext(String fileStorageDirectory, int polyModulus, int scale);

    public native static void nativeReleaseCryptoContext(long cryptoContext);

    public native static String nativeEncrypt(long cryptoContext, double[] values, String ciphertextPath);

    public native static double[] nativeDecrypt(long cryptoContext, String input);

    public native static boolean nativeLoadLocalKeys(long cryptoContext, String publicKeyPath, String secretKeyPath);

    public native static String nativeGenerateKeys(
            long cryptoContext,
            String publicKeyPath,
            String secretKeyPath,
            String relinearizeKeyPath);
}
