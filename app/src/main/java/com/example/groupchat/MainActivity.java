package com.example.groupchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.codebutler.android_websockets.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import cn.edu.buaa.crypto.algebra.serparams.PairingCipherSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.CPABEMHOOEngine;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOCiphertextInSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateOTSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.serparams.CPABEMHOOIntermediateSerParameter;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.CPABEMHOOAddress;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOODecryption;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOOEncryptionIn;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOOEncryptionOut;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOOKeyGen;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOOSetup;
import cn.edu.buaa.crypto.encryption.abe.cpabe.MHOO.test.MHOOUKeyGen;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.IBEBF01bEngine;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.serparams.IBEBF01bCiphertextSerParameter;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.serparams.IBEBF01bConcatSerParameter;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.test.IBEDecryption;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.test.IBEAddress;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.test.IBEEncryption;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.test.IBEKeyGen;
import cn.edu.buaa.crypto.encryption.ibe.bf01b.test.IBESetup;
import cn.edu.buaa.crypto.utils.AESCoder;
import cn.edu.buaa.crypto.utils.Base64Util;
import cn.edu.buaa.crypto.utils.PolicyUtil;
import cn.edu.buaa.crypto.utils.TestUtils;
import cn.edu.buaa.crypto.utils.ZipUtils;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class MainActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView showGroupName;
    private EditText inputMsg;
    private Button btCamera;
    private Button btAlbum;
    private Button btnSend;
    private EditText inputFileName;
    private EditText inputGroup;
    private Button btnRepost;
    private ImageView ivShow;
    private TextView tvShow;

    private TakePictureManager takePictureManager;
    private WebSocketClient client;
    private MTCNN mtcnn;
    private Facenet facenet;
    private JsonUtils mJson;

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    // Client name
    private String name = null;
    private int groupId;
    Map<Integer, String> CoUserID = new HashMap<Integer, String>();
    Map<String, int[]> faces = new HashMap<String, int[]>();
    Element s0;

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    //上传的压缩包的文件名
    String strFileName="";
    String recvFileName = "";
    String recvRepostFileName = "";


    private void initView() {
        showGroupName = (TextView) findViewById(R.id.showGroupName);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        btnSend = (Button) findViewById(R.id.btnSend);
        inputFileName = (EditText) findViewById(R.id.inputFileName);
        inputGroup = (EditText) findViewById(R.id.inputGroupId);
        btnRepost = (Button) findViewById(R.id.btnRepost);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);
        btCamera = (Button) findViewById(R.id.btCamera);
        btAlbum = (Button) findViewById(R.id.btAlbum);
        tvShow = (TextView) findViewById(R.id.tvShow);
        ivShow = (ImageView) findViewById(R.id.ivShow);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        //载入MTCNN和FaceNet模型
        mtcnn = new MTCNN(getAssets());
        facenet = new Facenet(getAssets());
        mJson = new JsonUtils(getApplicationContext());

        // 从上一个屏幕获取姓名
        Intent i = getIntent();
        name = i.getStringExtra("name");
        groupId = i.getIntExtra("groupId", 0);
        showGroupName.setText("群聊GroupID：" + groupId);

        //System Setup
//        MHOOSetup mMHOOSetup = new MHOOSetup();
//        mMHOOSetup.CPABEMHOOSetup("MHOO_PK.txt","MHOO_MK.txt");
//        IBESetup mIBESetup = new IBESetup();
//        mIBESetup.IBE01bSetup("IBE_PK.txt", "IBE_MK.txt");

        //User Registration and Group Initialization - sk
//        String comparableAttributeMaxValue = "scl:10 ts:1569809439999 te:1569809439999";
//        Map<String, Integer> binaryLength =PolicyUtil.getComparableAttributeBinaryLength(comparableAttributeMaxValue);
//        String[] Ali_S_satisfied = new String[] {"1", "acquaintance", "classmate", "scl=3", "ts=2019-09-30 08:10:30:360", "te=2020-01-01 08:10:30:360"};
//        String[] Bob_S_satisfied = new String[] {"1", "2", "acquaintance", "classmate", "friend", "scl=3", "ts=2019-09-10 08:10:30:360", "te=2020-01-01 08:10:30:360"};
//        String[] Eve_S_satisfied = new String[] {"1", "2", "acquaintance", "classmate", "friend", "scl=3", "ts=2019-09-10 08:10:30:360", "te=2020-01-01 08:10:30:360"};
//
//        Ali_S_satisfied  = PolicyUtil.attributeReplace( Ali_S_satisfied , binaryLength);
//        Bob_S_satisfied  = PolicyUtil.attributeReplace( Bob_S_satisfied , binaryLength);
//        Eve_S_satisfied  = PolicyUtil.attributeReplace( Bob_S_satisfied , binaryLength);
//        MHOOKeyGen mMHOOKeyGen = new MHOOKeyGen();
//        mMHOOKeyGen.CPABEMHOOKeyGen( "MHOO_PK.txt",  "MHOO_MK.txt", Ali_S_satisfied, "MHOO_SK_Ali.txt");
//        mMHOOKeyGen.CPABEMHOOKeyGen( "MHOO_PK.txt", "MHOO_MK.txt", Bob_S_satisfied, "MHOO_SK_Bob.txt");
//        //IBE.KeyGen(mk, u.id)
//        IBEKeyGen mIBEKeyGen = new IBEKeyGen();
//        mIBEKeyGen.IBE01bKeyGen("IBE_PK.txt", "IBE_MK.txt", "Ali", "IBE_SK_Ali.txt");
//        mIBEKeyGen.IBE01bKeyGen("IBE_PK.txt", "IBE_MK.txt", "Bob", "IBE_SK_Bob.txt");
//        mIBEKeyGen.IBE01bKeyGen("IBE_PK.txt", "IBE_MK.txt", "Eve", "IBE_SK_Eve.txt");
//        try {
//            JniUtils.createCryptoContext(CPABEMHOOAddress.CKKSAddress, 8192, 40);
//            JniUtils.keyGen(CPABEMHOOAddress.CKKSAddress);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        btAlbum.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureManager = new TakePictureManager(MainActivity.this);
                takePictureManager.setTailor(1, 1, 300, 300);//开启裁剪
                takePictureManager.startTakeWayByAlbum();
                takePictureManager.setTakePictureCallBackListener(new TakePictureManager.takePictureCallBackListener() {
                    @Override
                    public void successful(boolean isTailor, File outFile, Uri filePath) {
                        //TODO:对上传的图片MTCNN+FaceNet、置乱； 加密人脸特征、加密图片
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath.getPath());

                        //(1)MTCNN人脸检测,获得人脸区域坐标：left,top,right,bottom;(2)FaceNet人脸识别，提取人脸特征：float[]
                        Map<Integer, Integer> faceScrambKeys  = new HashMap<Integer, Integer>();
                        Map<Integer, int[]> faceRegions = new HashMap<Integer, int[]>();
                        int[][] faceRegionsArray = new int[2][4];
                        Map<Integer, float[]> faceFeatures = new HashMap<Integer, float[]>();
                        final Map<Integer, double[]> doubleFaceFeatures = new HashMap<Integer, double[]>();

                        //TODO: Face Encryption by Sender: 提取人脸特征，CKKS加密后发送至SP
                        MTCNNandFaceNet(bitmap, faceScrambKeys, faceRegions,faceRegionsArray, faceFeatures);

                        int[][] newFaceRegions = new int[2][5];
                        for (int i = 0; i < faceRegionsArray.length; i++) {
                            newFaceRegions[i][0] = faceScrambKeys.get(i);
                            for (int j = 0; j < 4; j++) {
                                newFaceRegions[i][j + 1] = faceRegionsArray[i][j];
                            }
                            Log.e("error", "Scrambling key:" + newFaceRegions[i][0]);
                            doubleFaceFeatures.put(i,convertFloatsToDoubles(faceFeatures.get(i))) ;
                        }

                        try {
                            JniUtils.createCryptoContext(CPABEMHOOAddress.CKKSAddress, 8192, 40);
                            //JniUtils.keyGen(CPABEMHOOAddress.CKKSAddress);//每次会产生不同的公私钥对，导致CloudServer解不出来
                            JniUtils.loadLocalKeys(CPABEMHOOAddress.CKKSAddress);//只能是X64CPU架构的手机才能正确运行
                            String encryptedFeature ="";
                            double[] decryptedFeature= new double[512];
                            for (Integer i: doubleFaceFeatures.keySet()){
                                encryptedFeature = JniUtils.encrypt(doubleFaceFeatures.get(i),CPABEMHOOAddress.CKKSAddress + File.separator +"encryptedFeature_"+i+".txt");
                                decryptedFeature = JniUtils.decrypt(encryptedFeature);
                                //decryptedFeature = JniUtils.decrypt( fileRead(CPABEMHOOAddress.CKKSAddress + File.separator +"ciphertextString.txt"));
                                Log.e("error", "原始的Feature[0]: " + doubleFaceFeatures.get(i)[0] + " " + doubleFaceFeatures.get(i)[511] );
                                Log.e("error", "解密后的Feature[0]: " + decryptedFeature[0] + " " + decryptedFeature[511]);
                            }
                            JniUtils.releaseCryptoContext();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        //TODO: 运行PPFR, the sender gets the id of co-users{wi.id}
                        //将加密的人脸特征文件发送至CloudServer
                        Thread featureThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                Client client = null; // 启动客户端连接，上传文件
                                try {
                                    for(int i =0;i<doubleFaceFeatures.size();i++){
                                        client = new Client(8122);
                                        client.uploadFeature("encryptedFeature_"+ i +".txt"); // 上传Feature
                                        client.close();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        try {
                            featureThread.start();
                            featureThread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Log.e("error", " 上传人脸特征结束 ");
                        //TODO: 将人脸特征消息发送至Social Provider
                        sendMessageToServer(mJson.getSendFeatureMessageJSON("FaceFeatures"));

                        //等待Social Provider 返回co-user的id
                        while(CoUserID.get(0) == null);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //使用从SP接收的co-user的ID加密
                        for (int i = 0; i < newFaceRegions.length; i++) {
                            faces.put(CoUserID.get(i), newFaceRegions[i]);//将人脸和co-user的id对应起来
                            Log.e("error", "CoUserID:"+ CoUserID.get(i));
                        }

                        //将faces保存至文件
                        //将人脸检测出的人脸位置坐标保存至文件
                        File file = new File(CPABEMHOOAddress.basicAddress + "faceRegions.txt");
                        FileWriter fw = null;
                        try {
                            fw = new FileWriter(file);
                            fw.write(faces.size() + "\r\n");
                            for(String couser: faces.keySet()){
                                int index = getKey(CoUserID, couser);
                                fw.write(index + " " + couser+ "\r\n");
                                for (int j = 0; j < 5; j++) {
                                    Log.e("error", "坐标：" + faces.get(couser)[j]);
                                    fw.write(faces.get(couser)[j] + " ");
                                }
                                fw.write("\r\n");
                            }
                            fw.flush();
                            fw.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //将人脸位置坐标文件发送至users
                        String regions= Base64Util.file2String(CPABEMHOOAddress.basicAddress + "faceRegions.txt");
                        sendMessageToServer(mJson.getSendRegionsMessageJSON(regions));

                        //置乱和解置乱
                        String scrambledPath = CPABEMHOOAddress.basicAddress + "scrambled.jpg";
                        String descrambledPath = CPABEMHOOAddress.basicAddress + "descrambled.jpg";
                        //调用Scrambling算法接口置乱人脸
                        String resultCode = JniUtils.scramblingBitmap(filePath.getPath(), newFaceRegions, scrambledPath);
                        System.out.println(resultCode);
                        //调用Descrambling算法接口恢复人脸
                        String resultCode2 = JniUtils.scramblingBitmap(scrambledPath, newFaceRegions, descrambledPath);
                        System.out.println(resultCode2);

                        //TODO:加密
                        //sender define a random key ek to encrypt the original photo o
                        PairingParameters pairingParameters = PairingFactory
                                .getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
                        Pairing pairing = PairingFactory.getPairing(pairingParameters);
                        String access_policy_T0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                        Element ek = pairing.getGT().newRandomElement().getImmutable();
                        Log.e("error", "ek:"+ ek);
                        CPABEMHOOCiphertextInSerParameter cipherInParameter = MHOOEncryptionIn.CPABEMHOOEncryptionIn("MHOO_PK.txt", access_policy_T0, filePath.getPath(), scrambledPath, faces, ek);
                        //Element s0 = cipherInParameter.getS0();
                        s0 = cipherInParameter.getS0();
                        Map<String, PairingCipherSerParameter> IT = cipherInParameter.getIT();
                        Map<String, PairingCipherSerParameter> ots = new HashMap<String, PairingCipherSerParameter>();
                        for (String key : IT.keySet()) {
                            CPABEMHOOIntermediateSerParameter it = (CPABEMHOOIntermediateSerParameter) (IT.get(key));
                            ots.put(key, it.getOt());
                        }

                        for (String key : ots.keySet()) {
                            CPABEMHOOIntermediateOTSerParameter oti = (CPABEMHOOIntermediateOTSerParameter) ots.get(key);
                            //TODO:sigma = (ek, oti)
                            try {
                                IBEBF01bConcatSerParameter sigma = new IBEBF01bConcatSerParameter(pairingParameters, ek, oti);
                                TestUtils.serialization(sigma,CPABEMHOOAddress.encryptedAddress + "IBE_Sigma_"+ key +".txt");

                                IBEEncryption.IBE01bEncryption("IBE_PK.txt", key, "IBE_Sigma_"+ key +".txt", "IBE_Head_"+ key +".txt", "IBE_Tau_" + key + ".txt");
                            } catch (IOException e) {
                                System.out.println("IO failed.");
                                e.printStackTrace();
                                System.exit(1);
                            }
                        }

                        //上传至Social Provider(实际上传加密文件至CloudServer，CloudServer返回文件名
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");//日期生成文件名
                        //strFileName =format.format(new Date())+ new Random().nextInt() + ".zip";
                        strFileName =format.format(new Date())+ ".zip";
                        String sdFile = Environment.getExternalStorageDirectory()+ File.separator + "GroupChat" + File.separator + "ciperZip";
                        Log.e("error",sdFile+ File.separator + strFileName);
                        File photoZipFile = new File(sdFile, strFileName); // 在android的sdcard上创建文件对象
                        try {
                            //压缩文件夹，参数为要压缩文件压缩后文件
                            ZipUtils.toZip(CPABEMHOOAddress.ZIPAddress, new FileOutputStream(photoZipFile), false);
                            //TODO: 上传文件
                            // Android 4.0 之后不能在主线程中请求HTTP或Socket
                            Thread thread = new Thread(new Runnable(){
                                @Override
                                public void run() {
                                    Client client = null; // 启动客户端连接，上传文件
                                    try {
                                        client = new Client(8120);
                                        recvFileName = client.uploadFile(strFileName); // 上传加密图片文件(文件名)
                                        client.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                            thread.start();
                            thread.join();

                            Log.e("error","recvFileName:" + recvFileName);
                            //Sender向SP发送从CloudServer返回的tag(文件名)
                            sendMessageToServer(mJson.getSendImgMessageJSON(recvFileName));

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Log.e("error","通知用户重新下载photo.zip");
                            sendMessageToServer(mJson.getSendNotifyUpdateMessageJSON(recvFileName));

                        } catch (IOException e) {
                            System.out.println("IO failed.");
                            e.printStackTrace();
                            System.exit(1);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        showToast("上传成功");
                    }
                    @Override
                    public void failed(int errorCode, List<String> deniedPermissions) {
                    }
                });
            }
        });

        btCamera.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureManager = new TakePictureManager(MainActivity.this);
                //开启裁剪 比例 1:3 宽高 350 350  (默认不裁剪)
                //takePictureManager.setTailor(1, 3, 350, 350);
                //拍照方式
                takePictureManager.startTakeWayByCarema();
                //回调
                takePictureManager.setTakePictureCallBackListener(new TakePictureManager.takePictureCallBackListener() {
                    //成功拿到图片,isTailor 是否裁剪？ ,outFile 拿到的文件 ,filePath拿到的URl
                    @Override
                    public void successful(boolean isTailor, File outFile, Uri filePath) {
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath.getPath());
                        //上传至服务器
                        String bitmapToString = bitmapToString(bitmap);//Bitmap压缩并转换为字符串
                        Log.e("error", "发送时图片长度" + bitmapToString.length());//7632B
                        sendMessageToServer(mJson.getSendImgMessageJSON(bitmapToString));
                        showToast("上传成功");
                    }
                    //失败回调
                    @Override
                    public void failed(int errorCode, List<String> deniedPermissions) {
                        Log.e("==w", deniedPermissions.toString());
                    }
                });
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sending message to web socket server
                sendMessageToServer(mJson.getSendTextMessageJSON(inputMsg.getText()
                        .toString()));
                // Clearing the input filed once message was sent
                inputMsg.setText("");
            }
        });

//        btnRepost.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 发送要转发的图片名和群聊ID发送给服务器
//                String access_policy_Tv0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
//                MHOOUKeyGen.CPABEMHOOUKeyGen("MHOO_PK.txt", access_policy_Tv0, s0); //生成uk
//                //上传至CloudServer
//                Thread thread = new Thread(new Runnable(){
//                    @Override
//                    public void run() {
//                        Client client = null; // 启动客户端连接，上传文件
//                        try {
//                            client = new Client(8126);
//                            recvRepostFileName = client.uploadUK("MHOO_UK.txt"); // 上传更新密钥MHOO_UK.txt
//                            client.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                thread.start();
//                try {
//                    thread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.e("error","uk上传完成");
//                Log.e("error","转发图片文件名：" + recvRepostFileName);
//                //sender发送消息msg=（热post，g', uk）至 Social Provider
////                sendMessageToServer(mJson.getSendRepostMessageJSON(
////                        inputFileName.getText().toString(), Integer.parseInt(inputGroup.getText().toString())));
//                sendMessageToServer(mJson.getSendRepostMessageJSON(
//                        recvRepostFileName, Integer.parseInt(inputGroup.getText().toString())));
//                inputFileName.setText("");
//                inputGroup.setText("");
//            }
//        });
        btnRepost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发送要转发的图片名和群聊ID发送给服务器
                String access_policy_Tv0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                MHOOUKeyGen.CPABEMHOOUKeyGen("MHOO_PK.txt", access_policy_Tv0, s0); //生成uk
                //将MHOO_UK.txt上传至CloudServer
                String uk = Base64Util.file2String(CPABEMHOOAddress.encryptedAddress + "MHOO_UK.txt");
                sendMessageToServer(mJson.getSendRepostMessageJSON(
                        uk, Integer.parseInt(inputGroup.getText().toString())));
                Log.e("error","uk上传完成");

                inputFileName.setText("");
                inputGroup.setText("");
            }
        });

        listMessages = new ArrayList<Message>();
        adapter = new MessagesListAdapter(this, listMessages, MainActivity.this);
        listViewMessages.setAdapter(adapter);

        /**
         * 创建web sockets客户端，回调函数
         * */
        client = new WebSocketClient(URI.create(WsConfig.URL_WEBSOCKET + URLEncoder.encode(name) + "&groupId=" + URLEncoder.encode(String.valueOf(groupId))),
                new WebSocketClient.Listener() {
                    @Override
                    public void onConnect() {
                    }

                    /**
                     * 从服务端接受消息
                     * */
                    @Override
                    public void onMessage(String message) {
                        Log.d(TAG, String.format("Got string message! %s", message));
                        //解析消息
                        parseMessage(message);
                    }

                    //In onMessage method parseMessage() is called to parse the JSON received from the socket server.
                    @Override
                    public void onMessage(byte[] data) {
                        Log.d(TAG, String.format("Got binary message! %s",
                                bytesToHex(data)));
                        // Message will be in JSON format
                        parseMessage(bytesToHex(data));
                    }

                    /**
                     * 连接中断
                     * */
                    @Override
                    public void onDisconnect(int code, String reason) {
                        String message = String.format(Locale.US,
                                "Disconnected! Code: %d Reason: %s", code, reason);
                        showToast(message);
                        // clear the session id from shared preferences
                        mJson.storeSessionId(null);
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e(TAG, "Error! : " + error);
                        showToast("Error! : " + error);
                    }
                }, null);
        client.connect();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (client != null & client.isConnected()) {
            client.disconnect();
        }
    }

    /****************************************************************自定义方法*************************************************************/
    /**
     * 使用MTCNN实现人脸检测
     *
     * @param bitmap
     */
    public void MTCNN(Bitmap bitmap) {
        Bitmap bm = Utils.copyBitmap(bitmap);
        // int[][] faceRegion=null;
        try {
            Vector<Box> boxes = mtcnn.detectFaces(bm, 40);//最小的脸像素值，一般>=40；返回所有人脸的Box
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "data.txt");
            Log.e("error", "文件路径：" + file.getPath());
            FileWriter fw = new FileWriter(file);
            // faceRegion = new int[boxes.size()][4];//一定要分配内存空间
            fw.write(boxes.size() + "\r\n");
            for (int i = 0; i < boxes.size(); i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                fw.write((i + 500) + " ");
                for (int j = 0; j < 4; j++) {
                    Log.e("error", "left：" + boxes.get(i).box[j]);
                    //写入一张人脸的left top right bottom
                    fw.write(boxes.get(i).box[j] + " ");
                }
                fw.write("\r\n");
            }
            fw.flush();
            fw.close();
            ivShow.setImageBitmap(bm);
        } catch (Exception e) {
            Log.e(TAG, "[*]detect false:" + e);
        }
    }
    /**
     * MTCNN + FaceNet
     *
     * @param bitmap
     */
    public void MTCNNandFaceNet(Bitmap bitmap) {
        Bitmap bm = Utils.copyBitmap(bitmap);

        Map<Integer, FaceFeature> map = new HashMap<Integer, FaceFeature>();
        List<Map<Integer, FaceFeature>> List_map = new ArrayList<>();
        List<FaceFeature> List_ff = new ArrayList<FaceFeature>();

        long t1 = System.currentTimeMillis();
        Vector<Box> boxes = mtcnn.detectFaces(bm, 40);
        Log.e("error", "检测人脸时间[ms]:" + String.valueOf(System.currentTimeMillis() - t1));

        if (boxes.size() == 0) {
            Log.e("error", "没有检测到人脸");
        }
        //将人脸检测出的人脸位置坐标保存至文件
        try {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "data.txt");
            Log.e("error", "文件路径：" + file.getPath());
            FileWriter fw = new FileWriter(file);
            fw.write(boxes.size() + "\r\n");
            for (int i = 0; i < boxes.size(); i++) {
                Utils.drawRect(bm, boxes.get(i).transform2Rect());
                Utils.drawPoints(bm, boxes.get(i).landmark);
                // 写入Scrambling 人脸区域时生成01用到的种子密钥key值
                fw.write((i + 500) + " ");

                for (int j = 0; j < 4; j++) {
                    Log.e("error", "left：" + boxes.get(i).box[j]);
                    //写入一张人脸的left top right bottom
                    fw.write(boxes.get(i).box[j] + " ");
                }
                fw.write("\r\n");
            }
            fw.flush();
            fw.close();
            ivShow.setImageBitmap(bm);
        } catch (Exception e) {
            Log.e(TAG, "[*]detect false:" + e);
        }

        //MTCNN检测到的人脸框，再上下左右扩展margin个像素点，再放入facenet中
        int margin = 20;//20这个值是facenet中设置的，可以调整
        long FR_Time = 0;
        for (int i = 0; i < boxes.size(); i++) {
            Rect rect = boxes.get(i).transform2Rect();
            Utils.rectExtend(bm, rect, margin);
            Bitmap face = Utils.crop(bm, rect);
            //特征提取
            long FR_Start = System.currentTimeMillis();
            FaceFeature ff = facenet.recognizeImage(face);
            long FR_End = System.currentTimeMillis();

            Log.e("error", "人脸特征时间[ms]: " + (FR_End - FR_Start));
            Log.e("error", "FaceFeature: " + ff.getFeature());
            FR_Time += FR_End - FR_Start;

            List_ff.add(ff);
            map.put(i, ff);
            List_map.add(map);
        }

        Log.e("error", "FaceNet获取人脸特征时间[ms]: " + FR_Time);
        Log.e("error", "人脸特征总数为：" + String.valueOf(List_ff.size()));

        //提取人脸特征并保存至文件
        try {
            float[] face = new float[512];
            File file = new File(CPABEMHOOAddress.basicAddress+ "faceFeature.txt");
            FileWriter fw = new FileWriter(file);
            fw.write(List_ff.size() + "\r\n");
            for (int i = 0; i < List_ff.size(); i++) {
                fw.write("第" + i + "张人脸的特征：\r\n");
                for (int j = 0; j < List_ff.get(i).getFeature().length; j++) {
                    face = List_ff.get(i).getFeature();
                    fw.write(face[j] + " ");
                }
                fw.write("\r\n");
            }
            fw.flush();
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List_ff.clear();
        map.clear();
        List_map.clear();
    }
    //人脸检测+faceNet特征提取
    public void MTCNNandFaceNet(Bitmap bitmap, Map<Integer, Integer> faceScrambKeys,  Map<Integer, int[]> faceRegions, int[][]faceRegions2, Map<Integer, float[]>faceFeatures) {
        Map<Integer, FaceFeature> map = new HashMap<Integer, FaceFeature>();
        List<Map<Integer, FaceFeature>> List_map = new ArrayList<>();
        List<FaceFeature> List_ff = new ArrayList<FaceFeature>();

        Vector<Box> boxes = mtcnn.detectFaces(bitmap, 40);

        if (boxes.size() == 0) {
            Log.e("error", "没有检测到人脸");
        }

        for (int i = 0; i < boxes.size(); i++) {
            Utils.drawRect(bitmap, boxes.get(i).transform2Rect());
            Utils.drawPoints(bitmap, boxes.get(i).landmark);
            // 写入Scrambling 人脸区域时生成01用到的种子密钥key值
            faceScrambKeys.put(i, i + 500);
            faceRegions.put(i, boxes.get(i).box);
            for (int j = 0; j < 4; j++) {
                Log.e("error", "left top right bottom:" + boxes.get(i).box[j]);
                faceRegions2[i][j] = boxes.get(i).box[j];
            }
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
            map.put(i, ff);
            List_map.add(map);
        }
        Log.e("error", "人脸特征总数为：" + String.valueOf(List_ff.size()));

        for (int i = 0; i < List_ff.size(); i++) {
            faceFeatures.put(i, List_ff.get(i).getFeature());
        }

        //提取人脸特征并保存至文件
        try {
            float[] face = new float[512];
            File file = new File(CPABEMHOOAddress.basicAddress+ "faceFeature.txt");
            FileWriter fw = new FileWriter(file);
            fw.write(List_ff.size() + "\r\n");
            for (int i = 0; i < List_ff.size(); i++) {
                Log.e("error", "FaceFeature的维度： " +  List_ff.get(0).getFeature().length);
                fw.write("第" + i + "张人脸的特征：\r\n");
                for (int j = 0; j < List_ff.get(i).getFeature().length; j++) {
                    face = List_ff.get(i).getFeature();
                    fw.write(face[j] + " ");
                }
                fw.write("\r\n");
            }
            fw.flush();
            fw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List_ff.clear();
        map.clear();
        List_map.clear();
    }

    /**
     * 发送消息
     * @param message
     */
    private void sendMessageToServer(String message) {
        if (client != null && client.isConnected()) {
            client.send(message);
        }
    }

    /**
     * 解析从服务端收到的json 消息的目的由flag字段所指定，flag=self，消息属于指定的人，
     * new：新人加入   *    到对话中，message：新的消息，exit：退出
     * @param msg
     */
    private void parseMessage(String msg) {
        try {
            JSONObject jObj = new JSONObject(msg);
            String flag = jObj.getString("flag");

            // 如果是self，json中包含sessionId信息
            if (flag.equalsIgnoreCase(TAG_SELF)) {
                String sessionId = jObj.getString("sessionId");
                // Save the session id in shared preferences
                mJson.storeSessionId(sessionId);
                Log.e(TAG, "Your session id: " + mJson.getSessionId());
            } else if (flag.equalsIgnoreCase(TAG_NEW)) {
                // If the flag is 'new', new person joined the room
                String name = jObj.getString("name");
                int groupId = jObj.getInt("groupId");
                String message = jObj.getString("message");
                String onlineCount = jObj.getString("onlineCount");
                showToast(name + message + ".  目前" + onlineCount
                        + " 人在群聊group" + groupId);
            }else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received
                String fromName = name;
                String message = jObj.getString("message");
                String sessionId = jObj.getString("sessionId");
                String fileName = jObj.getString("fileName");

                int type = jObj.getInt("type");
                boolean isSelf = true;
                // Checking if the message was sent by you
                if (!sessionId.equals(mJson.getSessionId())) {
                    fromName = jObj.getString("name");
                    isSelf = false;
                }

                //TODO:如果是图片名，从CloudServer下载文件
                if (type == 1) {
                    //根据接收到的文件名向CloudSever请求文件
                    Thread downloadFileThread = new downloadFileThread(message);
                    downloadFileThread.start();
                    downloadFileThread.join();
                    final String filename = ((downloadFileThread) downloadFileThread).getFilename();

                    Log.e("error", "从CloudServer的接收的文件在SD上的存储文件名："+ filename );
                    String unzipPath = CPABEMHOOAddress.rcvZIPAddress + filename;
                    File unZip = new File(unzipPath);
                    if(!unZip.exists()) {
                        unZip.mkdirs();
                    }
                    //将filename.zip文件压缩到filename文件夹下
                    ZipUtils.unZip(new File(CPABEMHOOAddress.rcvZIPAddress + filename +".zip"), unzipPath);

                    //TODO: Policy Aggregation
                    String path = filename + File.separator;
                    IBEDecryption.IBE01bDecryption("IBE_PK.txt","IBE_SK_"+ name +".txt", name, path+ "IBE_Head_"+ name +".txt", path+ "IBE_Tau_"+ name +".txt",path+ "IBE_anSigma_"+ name +".txt");
                    IBEBF01bConcatSerParameter anSigma = (IBEBF01bConcatSerParameter)TestUtils.deSerializationCipher(CPABEMHOOAddress.rcvZIPAddress + path + "IBE_anSigma_"+ name +".txt");
                    byte[] byteArrayEK = anSigma.getByteArrayEK();
                    byte[] byteArrayOTi = TestUtils.SerCipherParameter(anSigma.getOt());

                    PairingParameters pairingParameters = PairingFactory
                            .getPairingParameters(TestUtils.TEST_PAIRING_PARAMETERS_PATH_a_80_256);
                    Pairing pairing = PairingFactory.getPairing(pairingParameters);

                    //TODO:co-user解密o
                    AESCoder.decryptFile(byteArrayEK, IBEAddress.rcvZIPAddress + filename + File.separator + "hdr.txt", IBEAddress.rcvZIPAddress + filename + File.separator + "anPhoto_"+ name +".jpg");
                    //TODO:co-user追加策略
                    String access_policy_Ti = "";
                    if(name.equals("Ali")){
                        access_policy_Ti = "scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    }else if(name.equals("Bob")){
                        access_policy_Ti = "scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    }

                    PairingCipherSerParameter oti = (PairingCipherSerParameter)TestUtils.deserCipherParameters(byteArrayOTi);
                    PairingCipherSerParameter eti = (PairingCipherSerParameter)TestUtils.deSerializationCipher(CPABEMHOOAddress.rcvZIPAddress + filename+ File.separator+"et_"+name+".txt");
                    CPABEMHOOIntermediateSerParameter iti = new  CPABEMHOOIntermediateSerParameter(pairingParameters,oti, eti);
                    //EncryptionOut(pk,iti=(oti,eti),Ti)
                    MHOOEncryptionOut.CPABEMHOOEncryptionOut("MHOO_PK.txt", access_policy_Ti, iti, CPABEMHOOAddress.rcvZIPAddress + filename + File.separator, name);

                    //向CloudServer发送Cti.txt文件
                    try {
                        Thread CtThread = new Thread(new Runnable(){
                            @Override
                            public void run() {
                                Client client = null; // 启动客户端连接，上传文件
                                try {
                                    client = new Client(8123);
                                    client.uploadCTi(filename , "cti_"+ name + ".txt"); // 上传Feature
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        CtThread.start();
                        CtThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    type = 5;

                }else if(type == 4){
                    String[] userSplit = message.split(";");
                    for(String segment : userSplit) {
                        if(segment.contains(":")) {
                            String[] segmentSplit = segment.split(":");
                            CoUserID.put(Integer.valueOf(segmentSplit[0]), segmentSplit[1]);
                        }
                    }
                    for (int i = 0; i < CoUserID.size(); i++) {
                        Log.e("error", "按顺序CoUserID:"+ CoUserID.get(i));
                    }
                    isSelf = false;
                    fromName = jObj.getString("name"); //显示由Social Provider发送couser的id

                }else if(type == 5|| type == 3){//重新下载图片文件(photo.zip)
                    //根据接收到的文件名向CloudSever请求文件
                    Thread downloadFileThread = new downloadFileThread(message);
                    downloadFileThread.start();
                    downloadFileThread.join();
                    String refilename = ((downloadFileThread) downloadFileThread).getFilename();

                    Log.e("error", "从CloudServer的接收的文件在SD上的存储文件名："+ refilename );
                    String unzipPath = CPABEMHOOAddress.rcvZIPAddress + refilename;
                    File unZip = new File(unzipPath);
                    if(!unZip.exists()) {
                        unZip.mkdirs();
                    }
                    //将filename.zip文件压缩到filename文件夹下
                    ZipUtils.unZip(new File(CPABEMHOOAddress.rcvZIPAddress + refilename +".zip"), unzipPath);

                    //TODO：Photo Read
                    String access_policy_T0 = "";
                    if(type == 5){
                        access_policy_T0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    }else if(type == 3){//转发
                        access_policy_T0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    }
                    //String access_policy_T0 = "1 and acquaintance and classmate and scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    String access_policy_Alice = "scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";
                    String access_policy_Bob = "scl>=3 and ts<=2019-09-30;13:11:33:365 and te>=2019-09-30;13:11:33:365";

                    Map<String, String> accessPolicys = new HashMap<String, String>();
                    accessPolicys.put("sender", access_policy_T0);
                    accessPolicys.put("Ali", access_policy_Alice);
                    accessPolicys.put("Bob", access_policy_Bob);

                    String basicPath = CPABEMHOOAddress.rcvZIPAddress + refilename + File.separator;
                    Map<String, String> ctiPath = new HashMap<String, String>();
                    ctiPath.put("Ali",basicPath + "cti_Ali.txt" );
                    ctiPath.put("Bob",basicPath + "cti_Bob.txt" );

                    Log.e("error", "开始解密");
                    Map<String, BigInteger> scramblingKeys =
                            MHOODecryption.CPABEMHOODecryption("MHOO_PK.txt", "MHOO_SK_"+ name +".txt", accessPolicys, basicPath + "ct0.txt", ctiPath, basicPath);

                    for(String couser: scramblingKeys.keySet()){
                        Log.e("error", couser + ":" + scramblingKeys.get(couser));
                    }

                    //TODO:恢复人脸
                    //从文件中读取facesRegions至faces变量中
                    File file = new File(CPABEMHOOAddress.basicAddress+ "faceRegions.txt");
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String lineTxt = null;
                    // 从缓冲区中逐行读取代码，调用readLine()方法
                    int facenum = Integer.parseInt(br.readLine());
                    for(int i=0;i<facenum;i++){
                        lineTxt = br.readLine();
                        String[] indexCouser = lineTxt.split(" ");
                        CoUserID.put(Integer.parseInt(indexCouser[0]), indexCouser[1]);
                        lineTxt = br.readLine();
                        System.out.println(lineTxt); // 逐行输出文件内容
                        String[] strDatas = lineTxt.split(" ");
                        int[] intDatas = new int[5];
                        for( int j =0;j<5;j++){
                            intDatas[j] = Integer.parseInt(strDatas[j]);
                        }
                        faces.put(indexCouser[1], intDatas);
                    }
                    br.close();

                    for(String couser: faces.keySet()){
                        Log.e("error", couser + "---------------------------------------------" );
                        int[] region = faces.get(couser);
                        for(int i = 0; i<region.length; i++){
                            Log.e("error", ""+ region[i]);
                        }
                    }
                    //将解密出的置乱密钥和couser的人脸位置区域对应起来
                    int[][] newFaceRegions = new int[2][5];
                    int[] regions = new int[5];
                    for(String couser: scramblingKeys.keySet()){
                        regions = faces.get(couser);  //bug: couser不会获得faces,只能存到文件再读取
                        Log.e("error", couser + "---------------------------------------------" );
                        int scramblingKey = scramblingKeys.get(couser).intValue();
                        regions[0] = scramblingKey;
                        int index = getKey(CoUserID, couser);
                        Log.e("error", couser + "的索引:" + index);
                        newFaceRegions[index] = regions;
                        Log.e("error", couser + "的置乱密钥:" + newFaceRegions[index][0] + " " + newFaceRegions[index][1] + " "+ newFaceRegions[index][2] + " " + newFaceRegions[index][3] + " " + newFaceRegions[index][4]);
                    }

                    //修改成错误的scramblingKey,则不能正确解扰，用于测试
                    newFaceRegions[0][0] = 0;

                    //解置乱恢复人脸, 调用Descrambling算法接口恢复人脸
                    String resultCode = JniUtils.scramblingBitmap(basicPath + "anScrambledPhoto.jpg", newFaceRegions, basicPath + "anPhoto.jpg");
                    System.out.println(resultCode);

                    //将恢复的图片显示在view中
                    Bitmap descrambBitmap = BitmapFactory.decodeFile(basicPath + "anPhoto.jpg");
                    message = bitmapToString(descrambBitmap);
                    type = 1;
                    Log.e("error", name + "解密完成！");

                }else if(type == 6){//接收faceRegions.txt文件
                    Base64Util.String2File(message,CPABEMHOOAddress.basicAddress + "faceRegions.txt");
                }

                Message m = new Message(fromName, message, fileName, isSelf, type);
                // 把消息加入到arraylist中
                appendMessage(m);

            } else if (flag.equalsIgnoreCase(TAG_EXIT)) {
                // If the flag is 'exit', somebody left the conversation
                String name = jObj.getString("name");
                int groupId = jObj.getInt("groupId");
                String message = jObj.getString("message");
                showToast(name + message + groupId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //根据value值获取到对应的一个key值
    public static Integer getKey(Map<Integer,String> map,String value){
        Integer key = null;
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                key = entry.getKey();
            }
        }
        return key;
    }
    /**
     * 把消息放到listView里
     */
    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);
                //When a new message is received,
                // the message is added to list view data source and adapter.notifyDataSetChanged() is called to update the chat list.
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 按size压缩Bitmap
     *
     * @param image
     * @param size
     * @return Bitmap
     */
    private static Bitmap compressImage(Bitmap image, int size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 将String转换成Bitmap
     *
     * @param string
     * @return
     */
    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将Bitmap转换成String类型
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        byte[] bytes = baos.toByteArray();
        String string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * 将byte[]转换成String类型
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * float[]转成double[]
     * @param input
     * @return
     */
    public static double[][] convertFloatsToDoubles(float[][] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[][] output = new double[input.length][input[0].length];
        for(int i = 0; i<input.length ; i++){
            for (int j = 0; j < input[0].length; j++)
            {
                output[i][j] = input[i][j];
            }
        }
        return output;
    }
    public static double[] convertFloatsToDoubles(float[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }
    public static String fileRead(String filePath) throws Exception {
        File file = new File(filePath);//定义一个file对象，用来初始化FileReader
        FileReader reader = new FileReader(file);//定义一个fileReader对象，用来初始化BufferedReader
        BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
        StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
        String s = "";
        while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
            //sb.append(s);
            sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
            //System.out.println(s);
        }
        bReader.close();
        String str = sb.toString();
        return str;
    }

}