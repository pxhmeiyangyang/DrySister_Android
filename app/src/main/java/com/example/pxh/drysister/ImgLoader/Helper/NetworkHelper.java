package com.example.pxh.drysister.ImgLoader.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 网络加载图片相关类文件
 */
public class NetworkHelper {
    private static final String TAG = "NetworkHelper";
    private static final int IO_BUFFER_SIZE = 8 * 1024;

    /**
     * 根据URL下载图片的方法
     * @param picUrl 图片URL
     * @return
     */
    public static Bitmap downloadBitmapFromUrl(String picUrl){
        Bitmap bitmap = null;
        HttpURLConnection urlConnection = null;
        BufferedInputStream in = null;
        try {
            final URL url = new URL(picUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(),IO_BUFFER_SIZE);
            bitmap = BitmapFactory.decodeStream(in);
        }catch (final IOException e){
            Log.e(TAG,"下载图片出错："+ e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    /**
     * 根据URL下载图片的方法
     * @param picUrl
     * @return
     */
    public static byte[] downloadUrlToStream(String picUrl){
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            URL url = new URL(picUrl);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            if (connection.getResponseCode() == 200){
                in = connection.getInputStream();
                out = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int length;
                while ((length = in.read(bytes)) != -1){
                    out.write(bytes,0,length);
                }
                return  out.toByteArray();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                in.close();
                out.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * url转MD5的方法
     * @param url
     * @return
     */
    public static String hashKeyFormUrl(String url){
        String cachekey;
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(url.getBytes());
            cachekey = bytesToHexString(messageDigest.digest());
        }catch (NoSuchAlgorithmException e){
            cachekey = String.valueOf(url.hashCode());
        }
        return cachekey;
    }

    /**
     * 字节数组转MD5的方法
     * @param bytes 字节数组
     * @return MD5字符串
     */
    public static String bytesToHexString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0;i < bytes.length;i++){
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1){
                stringBuilder.append('0');
            }
            stringBuilder.append(hex);
        }
        return stringBuilder.toString();
    }

}
