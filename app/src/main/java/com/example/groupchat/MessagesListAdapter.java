package com.example.groupchat;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MessagesListAdapter extends BaseAdapter {
    private Activity mActivity;
    private Context context;
    private List<Message> messagesItems;
    public ImageView ivShow;

    Bitmap bitmap = null;

    public MessagesListAdapter(Context context, List<Message> navDrawerItems, Activity mActivity) {
        this.context = context;
        this.messagesItems = navDrawerItems;
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return messagesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        /**
         * The following list not implemented reusable list items as list items
         * are showing incorrect data Add the solution if you have one
         * */

        final Message m = messagesItems.get(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // Identifying the message owner
        if (messagesItems.get(position).isSelf()) {
            // message belongs to you, so load the right aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_right,
                    null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = mInflater.inflate(R.layout.list_item_message_left,
                    null);
        }

        TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        ivShow =(ImageView) convertView.findViewById(R.id.ivShow);

        if(messagesItems.get(position).getType() == 1 || messagesItems.get(position).getType() == 3){ //post图片或repost
            bitmap = stringToBitmap(m.getMessage());
            lblFrom.setText(m.getFromName());
            ivShow.setImageBitmap(bitmap);
            txtMsg.setBackgroundResource(0);//取消txtMsg的背景图片
        }else if(messagesItems.get(position).getType() == 2 ||messagesItems.get(position).getType() == 4){//文本 或 接收到co-user id（只有sender）
            lblFrom.setText(m.getFromName());
            txtMsg.setText(m.getMessage());
        }else if(messagesItems.get(position).getType() == 5){//接收到通知下载, 显示文件名
            lblFrom.setText(m.getFromName());
            txtMsg.setText(m.getMessage());
        }
        return convertView;
    }


    /**
     * 将String转换成Bitmap
     * @param string
     * @return
     */
    public static Bitmap stringToBitmap(String string)
    {
        Bitmap bitmap = null;
        try
        {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string,Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将bitmap压缩并转换成String
     * @param bitmap
     * @return
     */
    public static String bitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        byte[] bytes = baos.toByteArray();
        String string = Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }


}