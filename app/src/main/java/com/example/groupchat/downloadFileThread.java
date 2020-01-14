package com.example.groupchat;

import android.util.Log;

import java.io.IOException;

public class downloadFileThread extends Thread {
    private String downfilename;
    private String filename;

    public downloadFileThread(String filename){
        this.downfilename = filename;
    }

    public String getFilename(){
        return filename;
    }
    @Override
    public void run() {
        super.run();
        Client client = null; // 启动客户端连接，上传文件
        try {
            client = new Client(8121);
            filename = client.downloadFile(downfilename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
