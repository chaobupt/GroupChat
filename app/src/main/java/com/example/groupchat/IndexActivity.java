package com.example.groupchat;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class IndexActivity extends AppCompatActivity{
    Button btnInput;
    Button btnInitialize;
    Button btnLogin;

    public static MTCNN mtcnn;
    public  Facenet facenet;
    public static String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FACE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        facenet=new Facenet(getAssets());
        mtcnn=new MTCNN(getAssets());

        btnInput = (Button) findViewById(R.id.btn_input);
        btnInitialize = (Button) findViewById(R.id.btn_initialize);
        btnLogin = (Button) findViewById(R.id.btn_login);

        //绑定事件:录入人脸
        btnInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentInput = new Intent(IndexActivity.this, RegisterActivity.class);
                startActivity(intentInput);
            }
        });
        //绑定事件:初始化人脸数据库
        btnInitialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetFeature();
            }
        });
        //绑定事件:聊天室登录页面
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLogin = new Intent(IndexActivity.this, NameActivity.class);
                startActivity(intentLogin);
            }
        });
    }


    public void GetFeature(){
        List<String> getImagePath = getImagePathFromSD(filePath);
        List<Bitmap> bitmap_set = new ArrayList<Bitmap>();
        Map<String,FaceFeature > map1 = new HashMap<String,FaceFeature >();
        List<Map<String,FaceFeature>> List_map = new ArrayList<>();
        for(String data:getImagePath){
            try {
                FileInputStream fis  = new FileInputStream(data);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                bitmap_set.add(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.e("error",data);
        }
        int i=0;
        for (Bitmap bm:bitmap_set) {
            Log.e("error", "正在提取第"+ String.valueOf(i)+"个特征.......");
            Vector<Box> boxes=mtcnn.detectFaces(bm,40);
            if (boxes.size()==0) {
                Log.e("error","未检测到人脸");;
            }
            else {
                Rect rect1=boxes.get(0).transform2Rect();
                Utils.rectExtend(bm,rect1,20);
                Bitmap face_=Utils.crop(bm,rect1);
                FaceFeature face=facenet.recognizeImage(face_);
                map1.put(getImagePath.get(i),face);
                List_map.add(map1);
                i++;
//                progressBar2.incrementProgressBy((int)(i/bitmap_set.size()*100));
            }
        }
        Log.e("error","提取特征结束，开始保存......");

        try {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FACE"+ File.separator+"storeFeature.txt");
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
            for(Map<String,FaceFeature> map : List_map) {
                os.writeObject(map);
            }
            os.writeObject(null);
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("error", "总共特征数为："+ String.valueOf(List_map.size()));
        Log.e("error","-----------------保存结束-----------------");
        map1.clear();
        bitmap_set.clear();
        List_map.clear();
    }
    public  static List<String> getImagePathFromSD(String filePath) {
        // 图片列表
        List<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径   File.separator(/)
        //String filePath = Environment.getExternalStorageDirectory().toString() + File.separator+ "FACE";
        // Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "FACE";
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (File file : files) {
            if (checkIsImageFile(file.getPath())) {
                imagePathList.add(file.getPath());
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile;
        isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        isImageFile = FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif") || FileEnd.equals("jpeg") || FileEnd.equals("bmp");
        return isImageFile;
    }

}
