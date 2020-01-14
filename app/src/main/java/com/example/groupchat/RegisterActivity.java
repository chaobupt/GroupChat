package com.example.groupchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.CPABEMHOOAddress;

import static android.hardware.Camera.open;
import static com.example.groupchat.ImgUtils.saveImageToGallery;


public class RegisterActivity extends AppCompatActivity {
    private Button btnAlbum;
    private EditText textName;
    public ImageView ivShow;
    private TakePictureManager takePictureManager;
    public MTCNN mtcnn;
    public Facenet facenet;
    String name = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //载入MTCNN和FaceNet模型
        mtcnn = new MTCNN(getAssets());
        facenet = new Facenet(getAssets());

        textName = (EditText)findViewById(R.id.name);
        btnAlbum = (Button) findViewById(R.id.btn_album);
        ivShow= (ImageView) findViewById(R.id.iv_show);
        name = textName.getText().toString();

        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textName.getText().toString().trim().length() > 0) {
                    name = textName.getText().toString().trim();
                    Log.e("error", "用户名:" + name);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "请输入你的账号", Toast.LENGTH_LONG).show();
                }
                takePictureManager = new TakePictureManager(RegisterActivity.this);
                takePictureManager.setTailor(1, 1, 300, 300);//开启裁剪
                takePictureManager.startTakeWayByAlbum();
                takePictureManager.setTakePictureCallBackListener(new TakePictureManager.takePictureCallBackListener() {
                    @Override
                    public void successful(boolean isTailor, File outFile, Uri filePath) {
                        //TODO:对上传的图片MTCNN+FaceNet、置乱； 加密人脸特征、加密图片
                        Log.e("error", "图片的存储路径:" + filePath.getPath());
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath.getPath());
                        //显示选择的头像
                        ivShow.setImageBitmap(bitmap);

                        //提取人脸特征加密后发送至CloudServer
                        float[] faceFeatures = new float[512];
                        faceFeatures = MTCNNandFaceNet(bitmap);
                        double[] doubleFeatures = new double[512];
                        doubleFeatures = convertFloatsToDoubles(faceFeatures);

                        //CKKS加密
                        try {
                            JniUtils.createCryptoContext(CPABEMHOOAddress.CKKSAddress, 8192, 40);
                            //JniUtils.keyGen(CPABEMHOOAddress.CKKSAddress);
                            JniUtils.loadLocalKeys(CPABEMHOOAddress.CKKSAddress);//只能是X64CPU架构的手机才能正确运行
                            String encryptedFeature = "";
                            double[] decryptedFeature = new double[512];

                            encryptedFeature = JniUtils.encrypt(doubleFeatures, CPABEMHOOAddress.RegisterFaceAddress
                                    + "encryptedFeature_" + name + ".txt");
                            decryptedFeature = JniUtils.decrypt(encryptedFeature);
                            Log.e("error", "原始的Feature[0]: " + doubleFeatures[0]);
                            Log.e("error", "解密后的Feature[0]: " + decryptedFeature[0]);

                            JniUtils.releaseCryptoContext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //将加密的人脸特征文件发送至CloudServer
                        try {
                            Thread featureThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Client client = null; // 启动客户端连接，上传文件
                                    try {
                                        client = new Client(8124);
                                        client.uploadRegisterFeature("encryptedFeature_" + name + ".txt"); // 上传Feature
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            featureThread.start();
                            featureThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void failed(int errorCode, List<String> deniedPermissions) {
                    }
                });
            }
        });

    }


    //把本地的onActivityResult()方法回调绑定到对象
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        takePictureManager.attachToActivityForResult(requestCode, resultCode, data);
    }

    //onRequestPermissionsResult()方法权限回调绑定到对象
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        takePictureManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //人脸检测+faceNet特征提取
    public float[] MTCNNandFaceNet(Bitmap bitmap) {
        List<FaceFeature> List_ff = new ArrayList<FaceFeature>();
        Vector<Box> boxes = mtcnn.detectFaces(bitmap, 40);
        if (boxes.size() == 0) {
            Log.e("error", "没有检测到人脸");
        }

        for (int i = 0; i < boxes.size(); i++) {
            Utils.drawRect(bitmap, boxes.get(i).transform2Rect());
            Utils.drawPoints(bitmap, boxes.get(i).landmark);
        }

        //MTCNN检测到的人脸框，再上下左右扩展margin个像素点，再放入facenet中
        int margin = 20;//20这个值是facenet中设置的，可以调整
        for (int i = 0; i < boxes.size(); i++) {
            Rect rect = boxes.get(i).transform2Rect();
            Utils.rectExtend(bitmap, rect, margin);
            Bitmap face = Utils.crop(bitmap, rect);
            //特征提取
            FaceFeature ff = facenet.recognizeImage(face);
            Log.e("error", "FaceFeature: " + ff.getFeature());
            List_ff.add(ff);
            ivShow.setImageBitmap(bitmap);
        }
        float[] faceFeatures = new float[512];
        for (int i = 0; i < List_ff.size(); i++) {
            faceFeatures = List_ff.get(i).getFeature();
        }
        List_ff.clear();
        return faceFeatures;
    }

    public double[] convertFloatsToDoubles(float[] input) {
        if (input == null) {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

}



