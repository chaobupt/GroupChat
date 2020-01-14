package com.example.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class SealCKKSTestActivity extends AppCompatActivity {
    public static final int CIPHER_SIZE = 4096;
    //三星手机测试
    //String fileStorageDirectory = Environment.getExternalStorageDirectory() + File.separator + "seal";
    //private static final String CIPHERTEXTPATH1 =Environment.getExternalStorageDirectory() + File.separator+ "seal"+ File.separator + "ciphertextAS1.txt";
    //private static final String CIPHERTEXTPATH2 =Environment.getExternalStorageDirectory() + File.separator+ "seal"+ File.separator + "ciphertextAS2.txt";
    //华为手机测试
    String fileStorageDirectory = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + File.separator + "seal";
    String CIPHERTEXTPATH1 = fileStorageDirectory+ File.separator + "ciphertextAS1.txt";
    String CIPHERTEXTPATH2 = fileStorageDirectory+ File.separator + "ciphertextAS2.txt";

    //Android6.0之后的系统就需要动态申请权限，读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seal_ckkstest);

        TextView tv = (TextView)findViewById(R.id.tv_show);

        double[] arrayX = new double[CIPHER_SIZE];
        arrayX[0]=0.04891288;
        arrayX[1]=0.031262737;
        arrayX[2]=0.030423492;
        arrayX[3]=0.0616257;
        for(int i=0;i<4;i++){
            Log.e("error", "原始的X: " + arrayX[i]);
        }
        double[] arrayY = new double[CIPHER_SIZE];
        arrayY[0]=0.020476542;
        arrayY[1]=-0.08371191;
        arrayY[2]=-0.038826868;
        arrayY[3]=6.062781E-4;
        for(int i=0;i<4;i++){
            Log.e("error", "原始的Y: " + arrayY[i]);
        }

        //动态申请权限
        requestStoragePermissions(SealCKKSTestActivity.this);

        try {
            JniUtils.createCryptoContext(fileStorageDirectory, 8192, 40);

            JniUtils.keyGen(fileStorageDirectory);
            JniUtils.loadLocalKeys(fileStorageDirectory); //私钥无法从文件中获取
            String encryptedX = JniUtils.encrypt(arrayX,CIPHERTEXTPATH1);
            String encryptedY = JniUtils.encrypt(arrayY,CIPHERTEXTPATH2);

            Log.e("error", "密文1存储路径 " + CIPHERTEXTPATH1);
            Log.e("error", "密文2存储路径 " + CIPHERTEXTPATH2);
            tv.setText(encryptedX);

            double[] decryptedX = JniUtils.decrypt(encryptedX);
            double[] decryptedY = JniUtils.decrypt(encryptedY);
            for(int i=0;i<4;i++){
                Log.e("error", "解密后的X: " + decryptedX[i]);
            }
            for(int i=0;i<4;i++){
                Log.e("error", "解密后的Y: " + decryptedY[i]);
            }
            JniUtils.releaseCryptoContext();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted togrant permissions
     * @param activity
     */
    public static void requestStoragePermissions(AppCompatActivity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_PERMISSION_CODE );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
}
